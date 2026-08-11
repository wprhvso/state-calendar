# state-calendar

A mood calendar for Android. The whole app is one screen: a month of days, and a tap that
changes the colour of one.

* **Tap a day** — it moves one step along `white → red → yellow → green`, and green wraps
  back to white. Every day starts white.
* **Long press a day** — straight back to white, so overshooting the colour you wanted costs
  one gesture instead of three.
* **Swipe** left or right for the next month, or use the arrows. **Tap the month** for a
  picker that jumps to any month of any year between 1970 and 2100.

That is the entire feature set. There is nothing to configure, nothing to name, no entry to
fill in, and nothing that ever asks for a permission.

## The model

One Room table, one row per coloured day:

| Column | What it holds |
| --- | --- |
| `date` | The day itself, as an epoch day — the primary key, so a day cannot be marked twice |
| `state` | `1` red, `2` yellow, `3` green |

White is not stored. A white day is a day with no row, which is what makes "everything starts
white" a fact about the schema rather than a rule the code has to remember: an empty database
and a calendar of white days are the same thing. Cycling back to white deletes the row, so the
table only ever holds days you actually answered for, and a year of untouched calendar costs
nothing.

Dates are epoch days rather than timestamps. A day is a calendar fact, not an instant — storing
it as one would mean a day marked in one time zone quietly landing on its neighbour after a
flight.

## The screen

The month is a `HorizontalPager`, so swiping is the primary way to move and the arrows are the
secondary one. Both settle on the same state, and the label between them is driven by the
pager's target page, so it changes the moment a swipe commits rather than when it finishes.

Every grid is six weeks tall whether the month needs six or not. Months that fit in five would
otherwise resize the pager mid-swipe, and a calendar that changes height as you flick through it
reads as broken. The cell size is measured once from whichever runs out first, the width or the
height, and the weekday header is laid out from the same number — so the columns line up in
portrait, in landscape and at every font scale.

The week starts where the locale says it starts, and month and weekday names come from the
platform in the standalone grammatical form, which is the one that is correct on its own in a
header rather than inside a date.

## The colours

White, red, yellow, green — in that order, because it is the order everyone already reads on a
traffic light, and the only order in which the four colours have an obvious meaning without a
legend.

White is the absence of an answer, so it is drawn as the surface the calendar sits on: in the
light theme that is literally white, and in the dark theme it is the darkest surface tone. A grid
of thirty-one genuinely white tiles on an OLED screen at night would be a lamp, not a calendar,
and the state it stands for — *nothing recorded* — is better served by a tile that recedes. The
outline is what keeps the grid legible either way.

The other three are fixed colours rather than theme roles: they are the data, and data that
changed hue with the wallpaper would not be worth recording. Everything else on the screen —
the bar, the chips, the picker, the ring around today — is Material You, taken from the system
palette.

## What it does not do

No notifications, no reminders, no accounts, no export, no streaks. The app declares no
permissions at all: read the manifest and it is a launcher activity and nothing else. A mood
calendar that nags you is a different app, and a worse one.

The month's tally along the bottom is the only derived number in the app, and it is only ever
counting the four colours in the month you are looking at.

## Building

Requires [just](https://github.com/casey/just), a JDK 17+ and the Android SDK (platform 37,
build-tools 37.0.0).

| Command | Result |
| --- | --- |
| `just build` | signed release APK in `android/app/build/outputs/apk/release` |
| `just debug` | debug APK |
| `just test` | unit tests for the calendar arithmetic and the colour cycle |
| `just lint` | ktlint and detekt with the shared [qa-kotlin](https://github.com/wprhvso/qa-kotlin) config |
| `just fix` | same, with formatting applied |
| `just install` | install the built APK over adb and launch it |

Releases are cut by tagging: `just version 0.2.0 && just tag`.

## Notes on the stack

AGP 9.3.1 with built-in Kotlin (2.4.10), KSP 2.3.11, Room 3 (`androidx.room3`) on the
`AndroidSQLiteDriver`, Compose from BOM 2026.06.01, `minSdk 31`, `targetSdk 37`.

The same two omissions as its sibling [prn-tracker](https://github.com/wprhvso/prn-tracker):
Material 3 Expressive is still alpha and renaming public API, so the expressive feel here is
hand-rolled on stable Material 3 — springy motion, large rounded shapes, dynamic colour, tonal
surfaces; and `material-icons-extended` is frozen, so the four icons the app needs are Material
Symbols shipped as vector drawables.

All of the logic that can be tested without a device is in `domain` and has no Android imports:
the page-to-month mapping, the six-week grid, the month tally, the colour cycle and the display
names. The `ui` layer is arrangement, and `data` is thirty lines of Room.
