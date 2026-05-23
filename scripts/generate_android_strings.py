#!/usr/bin/env python3
"""Generate localized Android strings.xml files from a translation CSV."""

from __future__ import annotations

import argparse
import csv
import re
import sys
from collections import OrderedDict
from pathlib import Path
from typing import Iterable
from xml.etree import ElementTree
from xml.sax.saxutils import escape, quoteattr


DEFAULT_SOURCE = Path("app/src/main/res/values/strings.xml")
DEFAULT_RES_DIR = Path("app/src/main/res")
DEFAULT_CSV_PATTERN = "language.csv"
KEY_COLUMN = "key"
BASE_LANGUAGE_COLUMN = "ko"
ARRAY_SEPARATOR = "|"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Generate app/src/main/res/values-*/strings.xml from a CSV. "
            "The default values/strings.xml is never modified."
        ),
    )
    parser.add_argument(
        "csv_path",
        nargs="?",
        type=Path,
        help=f"Translation CSV path. Defaults to the single root {DEFAULT_CSV_PATTERN} file.",
    )
    parser.add_argument(
        "--source",
        type=Path,
        default=DEFAULT_SOURCE,
        help=f"Default Korean strings.xml. Defaults to {DEFAULT_SOURCE}.",
    )
    parser.add_argument(
        "--res-dir",
        type=Path,
        default=DEFAULT_RES_DIR,
        help=f"Android res directory. Defaults to {DEFAULT_RES_DIR}.",
    )
    parser.add_argument(
        "--languages",
        nargs="+",
        help=(
            "CSV language columns to generate. "
            f"Defaults to every column except {KEY_COLUMN} and {BASE_LANGUAGE_COLUMN}."
        ),
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print what would be generated without writing files.",
    )
    parser.add_argument(
        "--include-untranslatable",
        action="store_true",
        help=(
            "Also write resources marked translatable=\"false\" to locale files. "
            "By default they are omitted so Android falls back to the default values file."
        ),
    )
    return parser.parse_args()


def find_default_csv() -> Path:
    candidates = sorted(Path.cwd().glob(DEFAULT_CSV_PATTERN))
    if not candidates:
        raise FileNotFoundError(f"No {DEFAULT_CSV_PATTERN} file found in {Path.cwd()}")
    if len(candidates) > 1:
        formatted = "\n".join(f"  - {candidate}" for candidate in candidates)
        raise ValueError(f"Multiple {DEFAULT_CSV_PATTERN} files found:\n{formatted}")
    return candidates[0]


def parse_source(source_path: Path) -> ElementTree.Element:
    parser = ElementTree.XMLParser(target=ElementTree.TreeBuilder(insert_comments=True))
    return ElementTree.parse(source_path, parser=parser).getroot()


def read_csv(csv_path: Path) -> tuple[list[str], dict[str, dict[str, str]]]:
    with csv_path.open(encoding="utf-8-sig", newline="") as csv_file:
        reader = csv.DictReader(csv_file)
        if reader.fieldnames is None:
            raise ValueError(f"{csv_path} has no header row")

        fieldnames = [field.strip() for field in reader.fieldnames]
        if KEY_COLUMN not in fieldnames:
            raise ValueError(f"{csv_path} must contain a '{KEY_COLUMN}' column")

        rows: dict[str, dict[str, str]] = OrderedDict()
        for line_number, row in enumerate(reader, start=2):
            key = (row.get(KEY_COLUMN) or "").strip()
            if not key:
                continue
            if key in rows:
                raise ValueError(f"Duplicate key '{key}' in {csv_path}:{line_number}")
            rows[key] = {column: row.get(column, "") for column in fieldnames}

    return fieldnames, rows


def source_resource_names(root: ElementTree.Element) -> set[str]:
    return {
        child.attrib["name"]
        for child in root
        if isinstance(child.tag, str) and "name" in child.attrib
    }


def generated_languages(fieldnames: Iterable[str], requested: list[str] | None) -> list[str]:
    if requested is not None:
        return requested
    return [
        field
        for field in fieldnames
        if field not in {KEY_COLUMN, BASE_LANGUAGE_COLUMN} and field.strip()
    ]


def values_dir_name(language_tag: str) -> str:
    parts = re.split(r"[-_]", language_tag.strip())
    if len(parts) == 1:
        return f"values-{parts[0]}"
    if len(parts) == 2 and len(parts[1]) == 2:
        return f"values-{parts[0]}-r{parts[1].upper()}"
    return "values-b+" + "+".join(parts)


def has_csv_value(rows: dict[str, dict[str, str]], key: str, language: str) -> bool:
    value = rows.get(key, {}).get(language, "")
    return bool(value and value.strip())


def csv_value(rows: dict[str, dict[str, str]], key: str, language: str) -> str:
    return rows[key][language]


def source_string_value(element: ElementTree.Element) -> str:
    return element.text or ""


def source_array_values(element: ElementTree.Element) -> list[str]:
    return [item.text or "" for item in element if item.tag == "item"]


def array_values_from_csv(value: str) -> list[str]:
    return [item.strip() for item in value.split(ARRAY_SEPARATOR)]


def should_translate(element: ElementTree.Element) -> bool:
    return element.attrib.get("translatable") != "false"


def android_text(value: str) -> str:
    normalized = value.replace("\r\n", "\n").replace("\r", "\n").replace("\n", r"\n")
    normalized = re.sub(r"(?<!\\)'", r"\\'", normalized)
    return escape(normalized)


def attrs_text(attributes: dict[str, str]) -> str:
    return "".join(f" {name}={quoteattr(value)}" for name, value in attributes.items())


def render_comment(comment: ElementTree.Element) -> list[str]:
    return [f"    <!--{comment.text or ''}-->"]


def render_string(
    element: ElementTree.Element,
    rows: dict[str, dict[str, str]],
    language: str,
) -> tuple[list[str], bool]:
    key = element.attrib["name"]
    translated = should_translate(element) and has_csv_value(rows, key, language)
    value = csv_value(rows, key, language) if translated else source_string_value(element)
    return [f"    <string{attrs_text(element.attrib)}>{android_text(value)}</string>"], translated


def render_string_array(
    element: ElementTree.Element,
    rows: dict[str, dict[str, str]],
    language: str,
) -> tuple[list[str], bool]:
    key = element.attrib["name"]
    translated = should_translate(element) and has_csv_value(rows, key, language)
    values = (
        array_values_from_csv(csv_value(rows, key, language))
        if translated
        else source_array_values(element)
    )

    lines = [f"    <string-array{attrs_text(element.attrib)}>"]
    lines.extend(f"        <item>{android_text(item)}</item>" for item in values)
    lines.append("    </string-array>")
    return lines, translated


def render_fallback_element(element: ElementTree.Element) -> list[str]:
    text = ElementTree.tostring(element, encoding="unicode", short_empty_elements=False)
    return [f"    {line}" if line else line for line in text.splitlines()]


def render_file(
    root: ElementTree.Element,
    rows: dict[str, dict[str, str]],
    language: str,
    include_untranslatable: bool,
) -> tuple[str, int, int, int]:
    lines = [
        "<resources>",
        f"    <!-- Generated by scripts/generate_android_strings.py for '{language}'. -->",
        "    <!-- Do not edit directly; update the CSV and regenerate. -->",
    ]
    translated_count = 0
    fallback_count = 0
    skipped_untranslatable_count = 0
    previous_kind = "header"

    for child in root:
        if child.tag is ElementTree.Comment:
            if previous_kind in {"header", "resource"}:
                lines.append("")
            lines.extend(render_comment(child))
            previous_kind = "comment"
            continue

        if not isinstance(child.tag, str):
            continue

        if (
            child.tag in {"string", "string-array"}
            and "name" in child.attrib
            and not should_translate(child)
            and not include_untranslatable
        ):
            skipped_untranslatable_count += 1
            continue

        if child.tag == "string" and "name" in child.attrib:
            rendered, translated = render_string(child, rows, language)
        elif child.tag == "string-array" and "name" in child.attrib:
            rendered, translated = render_string_array(child, rows, language)
        else:
            rendered = render_fallback_element(child)
            translated = False

        lines.extend(rendered)
        previous_kind = "resource"
        if "name" in child.attrib:
            if translated:
                translated_count += 1
            else:
                fallback_count += 1

    lines.append("</resources>")
    return (
        "\n".join(lines) + "\n",
        translated_count,
        fallback_count,
        skipped_untranslatable_count,
    )


def write_generated_file(
    res_dir: Path,
    language: str,
    content: str,
    dry_run: bool,
) -> Path:
    output_dir = res_dir / values_dir_name(language)
    output_path = output_dir / "strings.xml"
    if dry_run:
        return output_path

    output_dir.mkdir(parents=True, exist_ok=True)
    output_path.write_text(content, encoding="utf-8")
    return output_path


def main() -> int:
    args = parse_args()
    csv_path = args.csv_path or find_default_csv()
    root = parse_source(args.source)
    fieldnames, rows = read_csv(csv_path)
    languages = generated_languages(fieldnames, args.languages)
    source_names = source_resource_names(root)
    extra_csv_keys = sorted(set(rows) - source_names)

    if not languages:
        raise ValueError("No language columns to generate")

    print(f"Source: {args.source}")
    print(f"CSV: {csv_path}")
    if extra_csv_keys:
        print(
            f"Skipping {len(extra_csv_keys)} CSV key(s) that are not in the source strings.xml: "
            + ", ".join(extra_csv_keys[:10])
            + (" ..." if len(extra_csv_keys) > 10 else "")
        )

    for language in languages:
        if language not in fieldnames:
            raise ValueError(f"CSV column '{language}' does not exist")

        content, translated_count, fallback_count, skipped_untranslatable_count = render_file(
            root,
            rows,
            language,
            args.include_untranslatable,
        )
        output_path = write_generated_file(args.res_dir, language, content, args.dry_run)
        action = "Would generate" if args.dry_run else "Generated"
        print(
            f"{action} {output_path} "
            f"({translated_count} translated, {fallback_count} fallback, "
            f"{skipped_untranslatable_count} default-only)"
        )

    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as error:
        print(f"error: {error}", file=sys.stderr)
        raise SystemExit(1)
