#!/bin/bash

set -eu

# Defaults
: ${TMPDIR="/tmp"}
: ${REVERSE_SSH_SLEEPTIME:=3600}
: ${REVERSE_SSH_REMOTE_PORT:=32022}

die () {
    local l_exitcode=${1}
    shift

    echo "${@}" >&2
    exit ${l_exitcode}
}

ssh_private_keyfile=`mktemp -q ${TMPDIR}/ssh-key-XXXXXX`

trap "/bin/rm -f ${ssh_private_keyfile}" 0 1 2 3 5 6 9 12 13 14 15

set +u
test -n "${REVERSE_SSH_HOST}" || die 1 "Environment variable 'REVERSE_SSH_HOST' must be provided"
test -n "${REVERSE_SSH_PRIVATE_KEY}" || die 1 "Environment variable 'REVERSE_SSH_PRIVATE_KEY' must be provided"
test -n "${REVERSE_SSH_PUBLIC_KEY}" || die 1 "Environment variable 'REVERSE_SSH_PUBLIC_KEY' must be provided"
set -u

echo "${REVERSE_SSH_PRIVATE_KEY}" > ${ssh_private_keyfile}

# enable reverse login
mkdir -p $HOME/.ssh
echo "${REVERSE_SSH_PUBLIC_KEY}" >> ${HOME}/.ssh/authorized_keys
chmod 755 $HOME
chmod 700 $HOME/.ssh
chmod 600 $HOME/.ssh/authorized_keys
# If all else fails login via password
# echo "runner:runner" | sudo -E chpasswd

unset SSH_AUTH_SOCK

set +e
ssh \
    -o StrictHostKeyChecking=no \
    -i ${ssh_private_keyfile} \
    -v ${REVERSE_SSH_HOST} \
    -R ${REVERSE_SSH_REMOTE_PORT}:localhost:22 \
    sleep ${REVERSE_SSH_SLEEPTIME}

sudo cat /var/log/auth.log