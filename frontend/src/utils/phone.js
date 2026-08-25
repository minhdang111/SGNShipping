export function formatPhoneNumber(value) {
  const digits = value.replace(/\D/g, '').slice(0, 10)

  if (digits.length < 10) return digits
  return `(${digits.slice(0, 3)}) ${digits.slice(3, 6)}-${digits.slice(6)}`
}

export function stripPhoneFormatting(value) {
  return value.replace(/\D/g, '').slice(0, 10)
}
