/*
 * existdb-set-password-cli - Utility to set the password of an eXist-db user account.
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

import se.softhouse.jargo.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

import static se.softhouse.jargo.Arguments.*;

public class Main {

    private static final int INVALID_ARGUMENT_EXIT_CODE = 3;
    private static final int INVALID_EXISTDB_VERSION_EXIT_CODE = 4;
    private static final int UNABLE_TO_OPEN_DATABASE_EXIT_CODE = 5;
    private static final int UNABLE_TO_SET_PASSWORD_EXIT_CODE = 6;

    public static void main(final String args[]) {
        // parse the command line arguments
        CommandLineOptions options = null;
        try {
            options = CommandLineOptions.parse(args);
        } catch (final ArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessageAndUsage());
            System.exit(INVALID_ARGUMENT_EXIT_CODE);
        }

        // find the Database implementation
        final DatabaseProvider databaseProvider = getDatabaseProvider(options.existdbVersion);

        // set the user's password
        try (final Database database = databaseProvider.open(options.existdbConfPath)) {
            database.setPassword(options.username, options.password);
        } catch (final DatabaseException e) {
            System.err.println("Could not open database: " + e.getMessage());
            System.exit(UNABLE_TO_OPEN_DATABASE_EXIT_CODE);
        } catch (final DatabaseSecurityException e) {
            System.err.println("Unable set password for user: " + options.username + ". " + e.getMessage());
            System.exit(UNABLE_TO_SET_PASSWORD_EXIT_CODE);
        }

        System.out.println("Password for user: '" + options.username + "' has been set.");
    }

    private static DatabaseProvider getDatabaseProvider(final String requiredExistDbVersion) {
        final ServiceLoader<DatabaseProvider> databaseProviderLoader = ServiceLoader.load(DatabaseProvider.class);
        for (final DatabaseProvider databaseProvider : databaseProviderLoader) {
            final String forVersion = databaseProvider.forVersion();
            if (requiredExistDbVersion.equals(forVersion)) {
                return databaseProvider;
            }
        }

        System.err.println("Could not find implementation of 'com.evolvedbinary.existdb.util.setpassword.DatabaseProvider' for eXist-db version: " + requiredExistDbVersion);
        System.exit(INVALID_EXISTDB_VERSION_EXIT_CODE);
        return null;
    }

    private static class CommandLineOptions {
        private static final Argument<?> helpArg = helpArgument("-h", "--help");

        private static final Argument<String> existdbVersionArg = stringArgument("-e", "--existdb-version")
                .description("The major version of eXist-db in which you wish to set a password, e.g. '6'.)")
                .defaultValue("6")
                .build();

        private static final Argument<File> existdbConfFileArg = fileArgument("-c", "--existdb-conf")
                .description("The path to the eXist-db conf.xml file.)")
                .required()
                .build();

        private static final Argument<String> usernameArg = stringArgument("-u", "--username")
                .description("The username of the user whose password you wish to set.)")
                .required()
                .build();

        private static final Argument<String> passwordArg = stringArgument("-p", "--password")
                .description("The new password to set for the user.)")
                .required()
                .build();

        public static CommandLineOptions parse(final String[] args) throws ArgumentException {
            final ParsedArguments arguments = CommandLineParser
                    .withArguments(existdbVersionArg, existdbConfFileArg, usernameArg, passwordArg)
                    .andArguments(helpArg)
                    .parse(args);

            final Path existdbConfPath = arguments.get(existdbConfFileArg).toPath();
            if (!Files.exists(existdbConfPath) && Files.isRegularFile(existdbConfPath)) {
                throw ArgumentExceptions.withMessage("The path to the conf.xml file must exist");
            }

            return new CommandLineOptions(arguments.get(existdbVersionArg), existdbConfPath, arguments.get(usernameArg), arguments.get(passwordArg));
        }

        final String existdbVersion;
        final Path existdbConfPath;
        final String username;
        final String password;

        public CommandLineOptions(final String existdbVersion, final Path existdbConfPath, final String username, final String password) {
            this.existdbVersion = existdbVersion;
            this.existdbConfPath = existdbConfPath;
            this.username = username;
            this.password = password;
        }
    }
}
