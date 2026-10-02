export default {
  app: { title: 'Payroll', signIn: 'Mag-sign in', signOut: 'Mag-sign out', language: 'Wika' },
  nav: { home: 'Home', employees: 'Mga empleyado', payroll: 'Mga payroll run', myPayslips: 'Aking payslips' },
  home: {
    welcome: 'Maligayang pagdating, {name}',
    intro: 'Demo ng HR at Payroll: Spring Boot API, sign-in gamit ang Keycloak, payslips sa S3 (LocalStack).',
    signedOut: 'Mag-sign in para magpatuloy. Demo users: ana / ana123 (empleyado), hana / hana123 (HR), paolo / paolo123 (payroll admin).',
    roles: 'Iyong mga role',
  },
  common: { search: 'Maghanap', all: 'Lahat', save: 'I-save', cancel: 'Kanselahin', loading: 'Naglo-load…', prev: 'Nakaraan', next: 'Susunod', page: 'Pahina {n} ng {total}', none: 'Wala pang laman.', error: 'Error', ref: 'Reference: {id}' },
  employees: {
    title: 'Mga empleyado', new: 'Bagong empleyado', edit: 'I-edit ang empleyado', no: 'Blg.', name: 'Pangalan', email: 'Email', username: 'Login username',
    type: 'Uri', salary: 'Batayang sahod', department: 'Departamento', active: 'Aktibo', searchHint: 'Pangalan o employee no.',
    saved: 'Nai-save.', REGULAR: 'Regular', CONTRACTUAL: 'Kontraktwal',
  },
  payroll: {
    title: 'Mga payroll run', period: 'Panahon', start: 'Patakbuhin ang payroll', status: 'Status', progress: 'Progreso', gross: 'Gross', deductions: 'Kaltas',
    net: 'Net', requestedBy: 'Hiniling ni', payslips: 'Payslips', started: 'Sinimulan ang payroll para sa {period}.',
    PENDING: 'Naghihintay', PROCESSING: 'Pinoproseso', COMPLETED: 'Tapos na', FAILED: 'Pumalya',
  },
  payslips: { title: 'Aking payslips', download: 'I-download ang PDF', empty: 'Wala ka pang payslip.', notLinked: 'Hindi naka-link ang login mo sa employee record.' },
}
