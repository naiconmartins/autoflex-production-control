package org.autoflex.application.gateways;

import java.util.Set;
import org.autoflex.application.dto.TokenData;

public interface TokenIssuer {

  TokenData issue(String subject, Set<String> roles);
}
