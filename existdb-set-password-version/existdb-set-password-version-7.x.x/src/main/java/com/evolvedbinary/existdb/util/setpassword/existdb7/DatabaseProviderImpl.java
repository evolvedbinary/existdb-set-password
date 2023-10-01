/*
 * existdb-set-password-version-7.x.x - Utility to set the password of an eXist-db user account.
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
import com.evolvedbinary.existdb.util.setpassword.DatabaseProvider;
import org.exist.EXistException;
import org.exist.storage.BrokerPool;
import org.exist.util.Configuration;
import org.exist.util.DatabaseConfigurationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.exist.repo.AutoDeploymentTrigger.AUTODEPLOY_PROPERTY;

public class DatabaseProviderImpl implements DatabaseProvider {

    @Override
    public String forVersion() {
        return "7";
    }

    @Override
    public Database open(final Path configFile) throws DatabaseException {
        final BrokerPool brokerPool = startDb(configFile);
        return new DatabaseImpl(brokerPool);
    }

    private static BrokerPool startDb(final Path configFile) throws DatabaseException {
        final String instanceName = BrokerPool.DEFAULT_INSTANCE_NAME;

        try {
            final Configuration config;
            if (configFile.isAbsolute() && Files.exists(configFile)) {
                config = new Configuration(configFile.toAbsolutePath().toString());
            } else {
                throw new DatabaseException("Configuration file does not exist: " + configFile);
            }

            System.setProperty(AUTODEPLOY_PROPERTY, "off");
            BrokerPool.configure(instanceName, 1, 5, config, Optional.empty());
            return BrokerPool.getInstance(instanceName);
        } catch (final DatabaseConfigurationException | EXistException e) {
            throw new DatabaseException(e);
        }
    }
}
