#!/usr/bin/env bash
export BASE_DIR="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"

function usage() {
    cat <<EOF
Usage: $0 [--static] [--build-tool=podman|docker]

Arguments:
  --build-tool - docker or podman
  --help       - print this
  --static     - build the native library statically linked
EOF
}

function build() {
    local tag="copycat:build"
    local build_tool="$1"
    local dockerfile="$2"
    local maven_version="3.9.12"

    if [[ -z "${build_tool}" ]]; then
        build_tool=$(get_build_tool)
    fi

    echo "-> Building docker image from ${dockerfile} using ${build_tool}"
    "${build_tool}" build -f "${BASE_DIR}/${dockerfile}" --build-arg MAVEN_VERSION="${maven_version}" -t "${tag}" "${BASE_DIR}/."
    echo "-> Extracting binary from the docker image"
    "${build_tool}" run --rm --entrypoint cat docker.io/library/copycat:build /project/target/copycat-0.0.1-runner >copycat
    chmod +x copycat
    "${build_tool}" rmi -f docker.io/library/copycat:build >/dev/null
}

function get_build_tool() {
    if type podman >/dev/null; then
        build_tool=podman
    elif type docker >/dev/null; then
        build_tool=docker
    else
        echo >&2 "Docker or podman is required to build."
        exit 1
    fi
}

function main() {
    local build_tool=""
    local dockerfile=src/main/docker/Dockerfile
    while [[ $# -gt 0 ]]; do
        case "$1" in
        --build-tool)
            shift
            build_tool="$1"
            ;;
        --help)
            usage
            exit 0
            ;;
        --static)
            dockerfile="${dockerfile}.static"
            ;;
        *)
            usage
            exit 1
            ;;
        esac
        shift
    done
    build "${build_tool}" "${dockerfile}"
}

main "$@"
