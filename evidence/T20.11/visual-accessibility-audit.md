# T20.11 Visual and Accessibility Gate

Date: 2026-09-19

Status: focused repair verified locally; release remains blocked for T20.12 and any later gates.

## Findings

- Bottom footer items exposed selected state but did not expose an accessible state description.
- Home icon/text controls relied on glyphs for the Budget Menu, previous/next month, quick action icons, and View Budgets.
- Setup Quest date picker trailing icons used calendar emoji without explicit accessible labels.
- Bill Entry and Bill Payment error banners used a warning emoji plus colored text; the row now exposes the full error message as status text.
- Budget Menu dismiss used an arrow glyph without an explicit label.

## Repairs

- Added footer state descriptions for selected and not selected tab states.
- Added explicit labels to Home Budget Menu, previous/next month, View Budgets, and quick action controls.
- Added explicit labels to Setup Quest payday and bill due-date picker icons.
- Added accessible error row descriptions to Bill Entry and Bill Payment banners.
- Added an explicit close label to the Budget Menu dismiss arrow.

## Verification

- `./gradlew testDebugUnitTest` — PASS.
- `./gradlew assembleDebug` — PASS.
- `./gradlew lintDebug` — initial parallel run failed from an internal lint missing-intermediate-manifest race while `assembleDebug` was running; standalone rerun PASS.

## Remaining

- No full visual screenshot matrix or TalkBack traversal was run in this increment.
- CI, release signing, shrinking, and provenance remain T20.12 scope.
