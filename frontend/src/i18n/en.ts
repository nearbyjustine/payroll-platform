export default {
  app: {
    title: 'Payroll', signIn: 'Sign in', signOut: 'Sign out', language: 'Language',
    theme: { system: 'Theme: system', dark: 'Theme: dark', light: 'Theme: light' },
  },
  nav: { home: 'Home', employees: 'Employees', payroll: 'Payroll runs', myPayslips: 'My payslips' },
  home: {
    welcome: 'Good day, {name}',
    intro: 'Employee records, monthly payroll runs and payslips: every deduction itemised, every run accounted for.',
    signedOutTitle: 'Payroll, itemised.',
    signedOut: 'Sign in with a demo account. The password is the username followed by 123.',
    roles: 'Signed in as',
    tasks: { employees: 'Maintain employee records', payroll: 'Run and review monthly payroll', payslips: 'Open your payslips' },
    demoRoles: { ana: 'Employee', hana: 'HR', paolo: 'Payroll admin' },
  },
  common: { search: 'Search', all: 'All departments', save: 'Save', cancel: 'Cancel', loading: 'Loading…', prev: 'Previous page', next: 'Next page', page: 'Page {n} of {total}', none: 'Nothing here yet.', ref: 'Reference {id}', pagination: 'Pagination' },
  employees: {
    title: 'Employees', lead: 'Search by name or employee number. Inactive employees are struck through.', new: 'New employee', edit: 'Edit',
    editTitle: 'Edit employee', no: 'No.', name: 'Name', email: 'Email', username: 'Login username', type: 'Type', salary: 'Base salary',
    department: 'Department', active: 'Active employee', searchHint: 'Name or employee no.', REGULAR: 'Regular', CONTRACTUAL: 'Contractual',
    empty: 'No employees match this search.', versionNote: 'If someone else saved this record after you opened it, saving will ask you to reload.',
  },
  payroll: {
    title: 'Payroll runs', lead: 'One run per month. Runs process in the background; the tape prints as employees are paid.',
    period: 'Period', start: 'Run payroll', status: 'Status', progress: 'Progress', gross: 'Gross', deductions: 'Deductions', net: 'Net',
    employees: 'Employees', employee: 'Employee', requestedBy: 'Requested by {user}', payslips: 'Payslips for {period}', started: 'Payroll for {period} accepted. Printing…',
    printing: 'Printing', register: 'Run register', noRuns: 'No runs yet. Choose a period and run payroll.', select: 'Select a run in the register.',
    PENDING: 'Pending', PROCESSING: 'Processing', COMPLETED: 'Posted', FAILED: 'Failed',
  },
  payslips: {
    title: 'My payslips', lead: 'Every deduction itemised. Downloads are private links that expire after five minutes.',
    download: 'Download PDF', empty: 'No payslips yet. They appear here after a payroll run.', gross: 'Gross pay', net: 'Net pay',
    disclaimer: 'Rates simplified for a learning project.',
  },
}
