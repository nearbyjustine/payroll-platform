export default {
  app: {
    title: 'Payroll', signIn: 'Mag-sign in', signOut: 'Mag-sign out', language: 'Wika',
    theme: { system: 'Tema: system', dark: 'Tema: madilim', light: 'Tema: maliwanag' },
  },
  nav: { home: 'Home', employees: 'Mga empleyado', payroll: 'Mga payroll run', myPayslips: 'Aking payslips' },
  home: {
    welcome: 'Magandang araw, {name}',
    intro: 'Mga record ng empleyado, buwanang payroll run at payslip: bawat kaltas nakalista, bawat run may tala.',
    signedOutTitle: 'Payroll, isa-isang nakalista.',
    signedOut: 'Mag-sign in gamit ang demo account. Ang password ay ang username na sinusundan ng 123.',
    roles: 'Naka-sign in bilang',
    tasks: { employees: 'Ayusin ang mga record ng empleyado', payroll: 'Patakbuhin at suriin ang buwanang payroll', payslips: 'Buksan ang iyong mga payslip' },
    demoRoles: { ana: 'Empleyado', hana: 'HR', paolo: 'Payroll admin' },
  },
  common: { search: 'Maghanap', all: 'Lahat ng departamento', save: 'I-save', cancel: 'Kanselahin', loading: 'Naglo-load…', prev: 'Nakaraang pahina', next: 'Susunod na pahina', page: 'Pahina {n} ng {total}', none: 'Wala pang laman.', ref: 'Reference {id}', pagination: 'Mga pahina' },
  employees: {
    title: 'Mga empleyado', lead: 'Maghanap ayon sa pangalan o employee number. May guhit ang mga hindi aktibo.', new: 'Bagong empleyado', edit: 'I-edit',
    editTitle: 'I-edit ang empleyado', no: 'Blg.', name: 'Pangalan', email: 'Email', username: 'Login username', type: 'Uri', salary: 'Batayang sahod',
    department: 'Departamento', active: 'Aktibong empleyado', searchHint: 'Pangalan o employee no.', REGULAR: 'Regular', CONTRACTUAL: 'Kontraktwal',
    empty: 'Walang empleyadong tugma sa paghahanap.', versionNote: 'Kung may ibang nag-save ng record na ito pagkatapos mong buksan, hihilingin sa iyong mag-reload.',
  },
  payroll: {
    title: 'Mga payroll run', lead: 'Isang run kada buwan. Tumatakbo ito sa background; nagpi-print ang tape habang binabayaran ang mga empleyado.',
    period: 'Panahon', start: 'Patakbuhin ang payroll', status: 'Status', progress: 'Progreso', gross: 'Gross', deductions: 'Kaltas', net: 'Net',
    employees: 'Mga empleyado', employee: 'Empleyado', requestedBy: 'Hiniling ni {user}', payslips: 'Mga payslip para sa {period}', started: 'Tinanggap ang payroll para sa {period}. Nagpi-print…',
    printing: 'Nagpi-print', register: 'Talaan ng mga run', noRuns: 'Wala pang run. Pumili ng panahon at patakbuhin ang payroll.', select: 'Pumili ng run sa talaan.',
    PENDING: 'Naghihintay', PROCESSING: 'Pinoproseso', COMPLETED: 'Naitala', FAILED: 'Pumalya',
  },
  payslips: {
    title: 'Aking payslips', lead: 'Nakalista ang bawat kaltas. Ang download ay pribadong link na mag-e-expire pagkalipas ng limang minuto.',
    download: 'I-download ang PDF', empty: 'Wala pang payslip. Lalabas ang mga ito pagkatapos ng payroll run.', gross: 'Gross na sahod', net: 'Net na sahod',
    disclaimer: 'Pinasimple ang mga rate para sa learning project.',
  },
}
