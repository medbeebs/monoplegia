#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
if [[ -f .env ]]; then
    set -a
    source .env
    set +a
fi
export GRADLE_USER_HOME="${MONOPLEGIA_GRADLE_USER_HOME:-$PWD/.gradle/user-home}"
if [[ $# -eq 0 ]]; then
    set -- build
fi
exec ./gradlew "$@"
