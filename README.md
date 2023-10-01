# eXist-db Set Password Utility

[![Build Status](https://github.com/evolvedbinary/existdb-set-password/actions/workflows/ci.yml/badge.svg)](https://github.com/evolvedbinary/existdb-set-password/actions/workflows/ci.yml)
[![Java 17](https://img.shields.io/badge/java-17-blue.svg)](https://adoptopenjdk.net/)
[![License](https://img.shields.io/badge/license-GPL%203.0-blue.svg)](https://www.gnu.org/licenses/gpl-3.0.html)

A utility that allows you to set the password of an eXist-db user account. It only requires access to the eXist-db on disk data folder and config file (`conf.xml`).

This can be useful if you are working with an eXist-db database, and you no longer know the password.

**NOTE** eXist-db must not be running when you execute this utility!

## Building from Source
```bash
git clone https://github.com/evolvedbinary/existdb-set-password.git
cd existdb-set-password
mvn clean package -P6
```

If you wish to build a version compatible with a different version of eXist-db, just replace the `6` in `-P6` with the major version of eXist-db that you need.

## Example - Set the password of the `admin` user

Assuming that `$EXIST_HOME` points to an exist-db 6.x.x installation where you wish to change the password of the Admin user.

```bash
existdb-set-password-cli/target/appassembly/bin/existdb-set-password.sh \
    --existdb-version 6 \
    --existdb-conf $EXIST_HOME/etc/conf.xml
    --username admin --password MY-NEW-PASSWORD
```
