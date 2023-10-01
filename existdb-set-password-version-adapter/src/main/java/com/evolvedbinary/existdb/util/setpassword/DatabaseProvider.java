/*
 * existdb-set-password - Utility to set the password of an eXist-db user account.
 * Copyright © 2023 Evolved Binary (hello@evolvedbinary.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.evolvedbinary.existdb.util.setpassword;

import java.nio.file.Path;

public interface DatabaseProvider {
    String forVersion();

    Database open(final Path configFile) throws DatabaseException;
}
