/**
 * Decode JWT payload (base64) without verifying signature.
 * Returns null for invalid tokens.
 */
export function decodeToken(token) {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const payload = parts[1]
    // Handle base64url → base64
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64)
    return JSON.parse(json)
  } catch {
    return null
  }
}

/**
 * Check if a JWT token is still valid (not expired).
 */
export function isTokenValid(token) {
  if (!token) return false
  const payload = decodeToken(token)
  if (!payload || !payload.exp) return false
  // exp is in seconds, Date.now() is in milliseconds
  return payload.exp * 1000 > Date.now()
}
