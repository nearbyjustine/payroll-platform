export default {
  app: { title: 'Payroll', signIn: 'Sign in', signOut: 'Sign out', language: 'Language' },
  nav: { home: 'Home', employees: 'Employees', payroll: 'Payroll runs', myPayslips: 'My payslips' },
  home: {
    welcome: 'Welcome, {name}',
    intro: 'HR & Payroll demo: Spring Boot API, Keycloak sign-in, payslips stored in S3 (LocalStack).',
    signedOut: 'Sign in to continue. Demo users: ana / ana123 (employee), hana / hana123 (HR), paolo / paolo123 (payroll admin).',
    roles: 'Your roles',
  },
  common: { search: 'Search', all: 'All', save: 'Save', cancel: 'Cancel', loading: 'Loading…', prev: 'Previous', next: 'Next', page: 'Page {n} of {total}', none: 'Nothing here yet.', error: 'Error', ref: 'Reference: {id}' },
  employees: {
    title: 'Employees', new: 'New employee', edit: 'Edit employee', no: 'No.', name: 'Name', email: 'Email', username: 'Login username',
    type: 'Type', salary: 'Base salary', department: 'Department', active: 'Active', searchHint: 'Name or employee no.',
    saved: 'Saved.', REGULAR: 'Regular', CONTRACTUAL: 'Contractual',
  },
  payroll: {
    title: 'Payroll runs', period: 'Period', start: 'Run payroll', status: 'Status', progress: 'Progress', gross: 'Gross', deductions: 'Deductions',
    net: 'Net', requestedBy: 'Requested by', payslips: 'Payslips', started: 'Payroll for {period} started.',
    PENDING: 'Pending', PROCESSING: 'Processing', COMPLETED: 'Completed', FAILED: 'Failed',
  },
  payslips: { title: 'My payslips', download: 'Download PDF', empty: 'You have no payslips yet.', notLinked: 'Your login is not linked to an employee record.' },
}
