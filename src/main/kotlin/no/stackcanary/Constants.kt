package no.stackcanary

// router constants
const val INVALID_PARAM_ID = "Invalid ID"
const val INVALID_AUTHORIZATION_HEADER = "Missing or invalid Authorization header"
const val INSUFFICIENT_SCOPE = "Access token has insufficient scope for this operation"
const val INVALID_TOKEN = "The access token is expired, revoked, malformed, or invalid for other reasons"

// scopes
const val SCOPE_READ = "employee.read"
const val SCOPE_EDIT = "employee.edit"
const val SCOPE_CREATE = "employee.create"
const val SCOPE_DELETE = "employee.delete"
