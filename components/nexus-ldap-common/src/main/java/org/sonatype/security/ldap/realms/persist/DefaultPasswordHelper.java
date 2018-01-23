/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.security.ldap.realms.persist;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.security.configuration.source.PhraseService;
import org.sonatype.security.ldap.upgrade.cipher.PlexusCipher;
import org.sonatype.security.ldap.upgrade.cipher.PlexusCipherException;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
@Named
public class DefaultPasswordHelper
    implements PasswordHelper
{

  private static final String ENC = "CMMDwoV";

  private final PlexusCipher plexusCipher;

  private final PhraseService phraseService;

  @Inject
  public DefaultPasswordHelper(final PlexusCipher plexusCipher, final PhraseService phraseService) {
    this.plexusCipher = checkNotNull(plexusCipher);
    this.phraseService = checkNotNull(phraseService);
  }

  @Override
  public String encrypt(String password)
      throws PlexusCipherException
  {
    if (password != null) {
      return phraseService.mark(plexusCipher.encrypt(password, phraseService.getPhrase(ENC)));
    }
    return null;
  }

  @Override
  public String decrypt(String encodedPassword)
      throws PlexusCipherException
  {
    if (encodedPassword != null) {
      if (phraseService.usesLegacyEncoding(encodedPassword)) {
        return plexusCipher.decrypt(encodedPassword, ENC);
      }
      return plexusCipher.decryptDecorated(encodedPassword, phraseService.getPhrase(ENC));
    }
    return null;
  }

  @Override
  public boolean foundLegacyEncoding() {
    return phraseService.foundLegacyEncoding();
  }
}
