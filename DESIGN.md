---
name: Payroll
description: Payroll as printed calculator tape. Every figure is itemised and every total is starred.
colors:
  desk: "#e1e4e7"
  desk-recessed: "#d4d8dc"
  paper: "#f9faf9"
  ink: "#15171a"
  ink-secondary: "#4a535b"
  rule: "#c7ccd1"
  ribbon: "#b3221a"
  ribbon-wash: "#f7dfdc"
  key: "#15171a"
  key-ink: "#f9faf9"
  posted: "#23663f"
  desk-night: "#101214"
  desk-night-recessed: "#1a1d20"
  paper-night: "#1f2225"
  ink-night: "#eceef0"
  ink-night-secondary: "#a5aeb6"
  rule-night: "#353a3f"
  ribbon-night: "#ff7d70"
  ribbon-night-wash: "#3b201d"
  posted-night: "#82d3a4"
  tape-lamplit: "#e4e6e2"
  tape-lamplit-ink: "#16181a"
  tape-lamplit-ink-secondary: "#4b545c"
  tape-lamplit-rule: "#b9bfc4"
  tape-lamplit-ribbon: "#a51f17"
  tape-lamplit-posted: "#1f5c38"
typography:
  display:
    fontFamily: "Schibsted Grotesk Variable, ui-sans-serif, system-ui, sans-serif"
    fontSize: "clamp(3rem, 7vw, 4.5rem)"
    fontWeight: 700
    lineHeight: 1.02
    letterSpacing: "-0.035em"
  headline:
    fontFamily: "Schibsted Grotesk Variable, ui-sans-serif, system-ui, sans-serif"
    fontSize: "2.25rem"
    fontWeight: 700
    lineHeight: 1.1
    letterSpacing: "-0.03em"
  body:
    fontFamily: "Schibsted Grotesk Variable, ui-sans-serif, system-ui, sans-serif"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.5
  body-ledger:
    fontFamily: "Schibsted Grotesk Variable, ui-sans-serif, system-ui, sans-serif"
    fontSize: "0.875rem"
    fontWeight: 400
    lineHeight: 1.43
  label:
    fontFamily: "Schibsted Grotesk Variable, ui-sans-serif, system-ui, sans-serif"
    fontSize: "0.75rem"
    fontWeight: 600
    letterSpacing: "0.05em"
  label-stamp:
    fontFamily: "Schibsted Grotesk Variable, ui-sans-serif, system-ui, sans-serif"
    fontSize: "0.68rem"
    fontWeight: 700
    letterSpacing: "0.08em"
  figure:
    fontFamily: "Martian Mono Variable, ui-monospace, SFMono-Regular, monospace"
    fontSize: "0.78rem"
    fontWeight: 400
    lineHeight: 2
    fontFeature: "tnum"
  figure-total:
    fontFamily: "Martian Mono Variable, ui-monospace, SFMono-Regular, monospace"
    fontSize: "0.92rem"
    fontWeight: 700
    lineHeight: 2
    fontFeature: "tnum"
  tape-meta:
    fontFamily: "Martian Mono Variable, ui-monospace, SFMono-Regular, monospace"
    fontSize: "0.66rem"
    fontWeight: 400
    letterSpacing: "0.08em"
rounded:
  stamp: "3px"
  key: "6px"
  surface: "6px"
  tape: "0px"
spacing:
  page-gutter: "1rem"
  page-gutter-wide: "1.5rem"
  ledger-cell-y: "0.62rem"
  ledger-cell-x: "0.8rem"
  tape-y: "1.35rem"
  tape-x: "1.2rem"
  section-gap: "2rem"
  control-height: "2.5rem"
components:
  button-key:
    backgroundColor: "{colors.key}"
    textColor: "{colors.key-ink}"
    rounded: "{rounded.key}"
    padding: "0.55rem 1rem"
    typography: "{typography.body-ledger}"
  button-key-quiet:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.key}"
    padding: "0.55rem 1rem"
  input:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.key}"
    padding: "0 0.7rem"
    height: "{spacing.control-height}"
  select:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.key}"
    padding: "0 2rem 0 0.7rem"
    height: "{spacing.control-height}"
  tape-strip:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.tape}"
    padding: "1.35rem 1.2rem"
    typography: "{typography.figure}"
  ledger-surface:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.surface}"
    typography: "{typography.body-ledger}"
  ledger-head:
    textColor: "{colors.ink-secondary}"
    padding: "0.65rem 0.8rem"
  stamp-posted:
    textColor: "{colors.posted}"
    rounded: "{rounded.stamp}"
    padding: "0.2rem 0.5rem"
    typography: "{typography.label-stamp}"
  stamp-failed:
    textColor: "{colors.ribbon}"
    rounded: "{rounded.stamp}"
    padding: "0.2rem 0.5rem"
  nav-link:
    textColor: "{colors.ink-secondary}"
    rounded: "{rounded.key}"
    padding: "0.375rem 0.75rem"
  nav-link-active:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.paper}"
    rounded: "{rounded.key}"
    padding: "0.375rem 0.75rem"
  error-banner:
    backgroundColor: "{colors.ribbon-wash}"
    textColor: "{colors.ribbon}"
    rounded: "{rounded.surface}"
    padding: "0.625rem 0.875rem"
---

# Design System: Payroll

## Overview

**Creative North Star: "The Adding-Machine Tape"**

Payroll is a cool grey desk with calculator tape lying on it. A payroll run and a payslip are printed strips of paper stock with torn zig-zag edges. Each line is a label, a figure and an operator column. Deductions print in red-ribbon ink, and the net pay is the total, marked with the calculator's `*`. Records that HR works on every day (employees, the run register, a run's payslips) stay as dense hairline ledgers on plain paper surfaces. The tape is kept for figures that are being added up.

The density is office-desktop working density: 14px ledger text, tabular figures and a 1180px column. The type does all the hierarchy work. Schibsted Grotesk sets the interface: headings, nav, labels and form text. Martian Mono appears only where something is a figure or is printed on the tape. Colour is close to monochrome, with ink on paper on desk. The only chromatic signals are ribbon red (negatives, failures, errors) and posted green (a completed run). This system replaces the KPI-card admin dashboard: there are no stat tiles and no chart panels, and totals sit on the tape where they are calculated.

Dark mode puts the same desk under a desk lamp at night. The desk and ledgers go dark, but the tape stays lit paper with dark print, because tape is a physical object and does not invert.

**Key Characteristics:**
- Cool grey desk (never cream) with paper surfaces lying on it under one soft shadow.
- Printed tape: torn zig-zag edges, a three-column grid of label, figure and operator, red `−` negatives and a starred bold total.
- Calculator key-cap buttons with a 2px bottom edge that sinks when pressed.
- Hairline ledgers: a 1.5px ink rule under the header, 1px rules between rows, no zebra striping.
- Mono for figures only, sans for everything a person reads as interface.
- Motion only on the live payroll run: the tape prints and its figures roll. Everything else renders at rest.

## Colors

The palette is a near-monochrome ink, paper and desk scheme with two functional inks borrowed from a two-colour printing calculator.

### Primary
- **Print Ink** (`ink`): the text colour, key-cap face (`key`), active nav pill, ledger header rule, focus outline and progress bar. In this system the "primary" colour is ink and not a hue.

### Secondary
- **Ribbon Red** (`ribbon`): every deduction figure and its `−` operator, failed-run stamps, field errors, the error banner text and the text caret. Red always means subtraction or something gone wrong, and is never used for decoration. **Ribbon Wash** (`ribbon-wash`) fills the error banner only.

### Tertiary
- **Posted Green** (`posted`): only the COMPLETED ("Posted") stamp.

### Neutral
- **Desk Grey** (`desk`): the page background. It is cool and slightly blue-grey, and the tape and ledgers lie on it.
- **Recessed Desk** (`desk-recessed`): hover and selected row fills, icon-button hover, skeletons and the progress track.
- **Tape Stock** (`paper`): the tape, ledger surfaces, form card and input fills.
- **Faded Print** (`ink-secondary`): leads, column headers, metadata, operators, idle icons. It measures 7.4:1 on paper.
- **Hairline** (`rule`): ledger row rules, dashed tape separators, input borders and the quiet key's edge.

### Night desk (dark theme, `.dark`)
`desk-night`, `desk-night-recessed`, `paper-night`, `ink-night`, `ink-night-secondary`, `rule-night`, `ribbon-night`, `ribbon-night-wash` and `posted-night` replace the neutrals above one for one. In dark mode the key cap inverts to a light face with dark ink.

### Lamp-lit tape (`tape-lamplit-*`)
In dark mode the `.tape` element re-scopes `--ink`, `--ink-2`, `--rule`, `--ribbon`, `--posted` and `--desk-2` to its own stock and print. As a result, a stamp, figure or rule placed on the tape stays dark-on-light. The tape surface also gets a soft radial lamp highlight from the top.

### Named Rules
**The Ribbon Rule.** Red ink means subtraction or failure and nothing else. Every deduction prints in ribbon red with a trailing `−` operator, on the tape and in ledgers (`3,330.05 −`), on desktop and on phones. A deduction never appears without its operator, and red never appears without a deduction or an error behind it.

**The Lit Tape Rule.** Tape is paper and never inverts. In dark mode the desk goes dark, but the tape remains light stock with dark print, using its own re-scoped ink tokens. Anything placed on a tape inherits the tape's tokens and does not use the page's tokens.

**The Cool Desk Rule.** The desk is cool grey. Warm cream or beige backgrounds belong to a different world.

## Typography

**Display Font:** Schibsted Grotesk Variable (with ui-sans-serif, system-ui)
**Body Font:** Schibsted Grotesk Variable
**Label/Mono Font:** Martian Mono Variable (with ui-monospace, SFMono-Regular), for figures and printed tape text only

**Character:** A compact, newsy grotesk for the person reading the interface, paired with a wide, mechanical mono that looks like it came out of a print head. The two never share a role.

### Hierarchy
- **Display** (700, 3rem rising to 4.5rem, line-height 1.02, -0.035em): the signed-out home headline only.
- **Headline** (700, 2.25rem, -0.03em): page titles (Employees, Payroll runs, My payslips). The form card uses a 1.875rem step of the same style.
- **Body** (400, 1rem, 1.5): leads in faded print, capped at 62ch.
- **Body Ledger** (400, 0.875rem): ledger cells, nav links, form text and key labels (600).
- **Label** (600, 0.75rem, 0.05em, uppercase): section headings ("Run register") and, at 0.7rem, ledger column headers. Set in sans.
- **Stamp** (700, 0.68rem, 0.08em, uppercase): the status stamp.
- **Figure** (Martian Mono, 0.78rem, tabular-nums; line-height 2 on tape): every money amount, count, employee number and period input.
- **Figure Total** (Martian Mono 700, 0.92rem): the starred total line.
- **Tape Meta** (Martian Mono, 0.66rem, 0.08em, uppercase): the printed header of a tape (period, employee, run number). This is printed tape text, so it uses mono.

### Named Rules
**The Print-Head Rule.** Mono is used only for figures and for text printed on tape. UI labels, nav, column headers, section headings, buttons and stamps are set in Schibsted Grotesk. To check a use of mono, ask whether a calculator would have printed it. If it would not, set it in sans.

**The Tabular Rule.** Every figure is set in tabular numerals and right-aligned in its column, so the decimal points line up.

## Layout

The page is a single centred column, at most 1180px wide, with a 1rem gutter (1.5rem from 640px up). Vertical padding is 1.75rem, rising to 2.5rem. The header is a hairline-ruled bar. On phones the nav wraps to its own scrollable row below the title.

The Payroll runs screen is the signature composition. At 1024px and wider, a 340px sticky left column holds the period input, the Run key and the active run's tape. A fluid right column holds the run register and, below it, the selected run's payslips ledger, with 2rem gaps between them. Below 1024px the columns stack. My payslips lays tapes out in a 1, 2 or 3 column grid (2rem column gap, 3rem row gap), and each tape has its quiet Download key below it.

### Named Rules
**The Two-Line Entry Rule.** Below 640px, every ledger becomes a list of two-line entries and keeps every field. Line one holds the name or period on the left and the bold figure on the right. Line two holds the secondary fields in faded print: mono number, department and type, or status stamp and count, or gross `+` and red deductions `−`. Columns are never dropped on phones.

## Elevation & Depth

Depth comes from one physical relationship: paper lying on a desk. Every paper surface (tape, ledger, form card, empty state) uses the same soft two-layer offset shadow, `--shadow` (light: `0 1px 1px rgb(21 23 26 / 0.06), 0 8px 20px -8px rgb(21 23 26 / 0.22)`; dark: `0 1px 1px rgb(0 0 0 / 0.45), 0 12px 26px -10px rgb(0 0 0 / 0.7)`). There are no stacked elevation levels. The only other shadow in the system is the key cap's 2px bottom edge, which is a mechanical depth and not a lift.

### Shadow Vocabulary
- **Paper on desk** (`var(--shadow)`): tapes and all paper surfaces. Static, with no hover lift.
- **Key edge** (`0 2px 0 rgb(0 0 0 / 0.35), inset 0 1px 0 rgb(255 255 255 / 0.12)`): key caps. It grows to 3px on hover and drops to 0 on press. The quiet key uses `0 2px 0 var(--rule)` plus an inset 1px rule ring.

### Named Rules
**The One Sheet Rule.** A surface is either on the desk (it gets the paper shadow) or part of the desk. Paper is never stacked on paper.

## Shapes

Tape is cut square, with 6px zig-zag tear teeth along its top and bottom edges (an SVG mask in 10px repeats, filled with tape stock). The tear marks a printed result and nothing else. Ledger surfaces, the form card, inputs and keys use a modest 6px radius. Stamps use 3px with a 1px border in their text colour, like a rubber stamp. Separators inside tape and form footers are 1px dashed rules, like a perforation. Separators in ledgers are solid hairlines.

## Components

### Buttons (calculator keys)
Keys are the only buttons in the system, and they should feel tactile and mechanical.
- **Shape:** 6px radius, padding 0.55rem 1rem, 600 weight at 0.875rem, with an icon gap of 0.5rem.
- **Primary key:** an ink face with paper legend (inverted in dark mode).
- **Hover / Press:** the key rises 1px and its bottom edge grows to 3px. On press it sinks 2px and the edge disappears. 120ms out-expo. Disabled keys show at 40% opacity. Reduced motion turns off all transforms.
- **Quiet key:** a paper face with ink legend and a rule-coloured edge. Used for secondary actions such as Download PDF, Cancel and Pager.
- **Icon buttons** (theme, sign out, row download/edit): bare, 6px radius, filled with recessed desk on hover.

### Inputs / Fields
- **Style:** all controls share one 2.5rem height. Paper fill, 1px rule border, 6px radius, 0.9rem text, and a ribbon-red caret. Labels sit above the control in faded sans at 0.8rem.
- **Select:** native appearance is removed and replaced by a 12px palette chevron on the right.
- **Focus:** the border turns ink and gains a 3px 12% ink halo. A global 2px ink focus outline covers everything else.
- **Error:** a ribbon-red 0.75rem line reserved under each field. Figure inputs (period, salary, employee number) are set in mono.

### Ledger
- A paper surface with a 6px radius and the paper shadow. Header cells use 0.7rem uppercase sans in faded print over a 1.5px ink rule. Rows have 1px hairlines, 0.62rem by 0.8rem cells and a 4% ink tint on hover. The selected row is filled with recessed desk. Figure cells are right-aligned mono. Inactive employees are struck through in faded print. There is no zebra striping.

### Tape Strip (signature)
- Paper stock with tear edges and padding of 1.35rem by 1.2rem. It opens with mono uppercase meta lines above a dashed rule. Each line is a grid of `1fr auto 1.2ch` (label, figure, operator). Operators are `+` for gross, `#` for count, `−` in ribbon for deductions, and `*` for the total. The total line is bold at 0.92rem above a dashed rule.
- The live run tape adds a status stamp in its head, a blinking print-head cursor while the run processes, and a 4px ink progress bar on a recessed track.

### Status Stamp
- A 1px current-colour border with 3px radius, uppercase sans at 0.68rem/700 and a 12px icon. Posted is green, Failed is red, and Pending/Processing are faded print (the Processing icon spins unless reduced motion is on).

### Navigation
- A sans 0.875rem/500 link in faded print. Hover adds a recessed desk fill. The active link is an ink pill with paper text. On phones the nav takes its own horizontally scrollable row.

### Error Banner
- A ribbon-wash fill with ribbon text, 6px radius and a circle-alert icon. The correlation ID shows below in 0.68rem mono, because it is a reference the user reads out to support.

### Motion
- **The Live Tape Rule.** Only the active payroll run's tape animates. On selection it unrolls downward (clip-path, 0.45s). While a run processes its figures roll to new values (0.6s), and the starred total prints in once it posts (0.3s). All motion uses the out-expo curve `cubic-bezier(0.16, 1, 0.3, 1)`. Payslip tapes, the home tape, ledger rows and everything else render at rest. Route changes use only a 0.18s fade with a 6px rise. Reduced motion is respected everywhere: numbers jump to their final value and the pulse and spin stop.

## Do's and Don'ts

### Do:
- **Do** print every deduction in ribbon red with a trailing `−` operator, on tape, in ledgers and in phone entries.
- **Do** end every itemised calculation with a bold, starred `*` total.
- **Do** set figures in Martian Mono with tabular numerals, right-aligned. Set every other piece of interface text in Schibsted Grotesk.
- **Do** keep the tape as lit paper stock with dark print in dark mode, using the tape's re-scoped tokens for anything printed on it.
- **Do** turn ledgers into two-line entries below 640px that keep every field.
- **Do** keep every form control at the shared 2.5rem height, with the palette chevron on selects.
- **Do** use calculator keys (ink or quiet) for every button-shaped action.

### Don't:
- **Don't** build KPI-card dashboards, stat tiles or chart panels. Totals belong on the tape.
- **Don't** set UI labels, nav, column headers or buttons in mono.
- **Don't** animate payslip tapes, ledger rows or page furniture. Motion belongs to the live run.
- **Don't** drop columns on phones or hide figures behind a click.
- **Don't** use red for anything except subtraction and failure, or green for anything except a posted run.
- **Don't** use zebra striping in ledgers, or stack paper on paper.
- **Don't** use warm cream desks, and don't invert the tape to dark stock.
