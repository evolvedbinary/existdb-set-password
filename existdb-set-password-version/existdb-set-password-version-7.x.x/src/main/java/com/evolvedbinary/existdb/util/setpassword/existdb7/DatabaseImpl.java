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
package com.evolvedbinary.existdb.util.setpassword.existdb7;

import com.evolvedbinary.existdb.util.setpassword.Database;
import com.evolvedbinary.existdb.util.setpassword.DatabaseException;
import com.evolvedbinary.existdb.util.setpassword.DatabaseSecurityException;
import org.exist.EXistException;
import org.exist.security.Account;
import org.exist.security.PermissionDeniedException;
import org.exist.security.SecurityManager;
import org.exist.storage.BrokerPool;

import javax.annotation.Nullable;

public class DatabaseImpl implements Database {
    private BrokerPool brokerPool;

    DatabaseImpl(final BrokerPool brokerPool) {
        this.brokerPool = brokerPool;
    }

    @Override
    public void close() {
        brokerPool.shutdown();
    }

    @Override
    public void setPassword(final String username, final String password) throws DatabaseSecurityException, DatabaseException {
        final SecurityManager securityManager = brokerPool.getSecurityManager();

        @Nullable final Account account = securityManager.getAccount(username);
        if (account == null) {
            throw new DatabaseSecurityException("No such account for: " + username);
        }

        account.setPassword(password);
        try {
            securityManager.updateAccount(account);
        } catch (final PermissionDeniedException e) {
            throw new DatabaseSecurityException(e);
        } catch (final EXistException e) {
            throw new DatabaseException(e);
        }
    }
}
