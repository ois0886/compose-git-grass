#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "$ROOT_DIR"

fail() {
  echo "ERROR: $1" >&2
  exit 1
}

LIB_VERSION=$(sed -nE 's/.*coordinates\("[^"]+", *"[^"]+", *"([^"]+)".*/\1/p' library/build.gradle.kts | head -n 1)
[[ -n "$LIB_VERSION" ]] || fail "library/build.gradle.kts에서 버전을 추출하지 못했습니다."

echo "Detected library version: $LIB_VERSION"

README_COORDINATE="implementation(\"io.github.ois0886:compose-git-grass:${LIB_VERSION}\")"
if ! grep -Fq "$README_COORDINATE" README.md; then
  fail "README.md 의 의존성 버전이 라이브러리 버전(${LIB_VERSION})과 다릅니다."
fi

if ! grep -Eq "^## \[${LIB_VERSION//./\\.}\] - " CHANGELOG.md; then
  fail "CHANGELOG.md 에 버전 섹션 '## [${LIB_VERSION}] - YYYY-MM-DD' 이 없습니다."
fi

UNRELEASED_LINK="[Unreleased]: https://github.com/ois0886/compose-git-grass/compare/v${LIB_VERSION}...HEAD"
if ! grep -Fq "$UNRELEASED_LINK" CHANGELOG.md; then
  fail "CHANGELOG.md 의 [Unreleased] 비교 링크가 v${LIB_VERSION} 기준이 아닙니다."
fi

echo "Documentation sync verification passed."
