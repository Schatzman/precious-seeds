#!/usr/bin/env bash
# Normalize src/main/resources PNGs: 8-bit RGBA, strip metadata, oxipng.
# Requires: ImageMagick (magick), oxipng (brew install oxipng).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
RES="$ROOT/src/main/resources"
if [[ ! -d "$RES" ]]; then
  echo "Missing $RES" >&2
  exit 1
fi
while IFS= read -r -d '' f; do
  magick "$f" -depth 8 -strip "$f"
  oxipng -o 3 --strip all "$f"
done < <(find "$RES" -name '*.png' -type f -print0 | sort -z)
echo "Processed PNGs under $RES"
