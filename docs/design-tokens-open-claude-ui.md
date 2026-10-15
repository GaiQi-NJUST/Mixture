# Design tokens, open-claude-ui

Source: https://github.com/Caho1/open-claude-ui, MIT, code-examples/cds-theme.css. Do not copy Claude proprietary marks or unlicensed material. Keep the MIT notice when MIT code is used.

## Colors

- Clay main: #d97757, hover #cc5a36, very light background #fbf3ef.
- Light Surface: 0 #f5f5f3, global background. 1 #fcfcfb, sidebar. 2 #ffffff, bubble and card. 3 #ffffff, floating panel and input box.
- Dark Surface: 0 #0b0b0a, 1 #151515, 2 #1a1a19, 3 #20201e, Popover #222220.
- Text: primary #151515, secondary #525252, muted #8a8a8a.
- User bubble: 18px radius with a light blue tinted background. Assistant: zero border, transparent layout.

## Radii

Composer outer frame 20px, message bubble 18px, panel 10px, button 6, 8, and 10px, Dialog 16px.

## Borders and shadows

Hairline border 1px, with 0.5px compensation on high density screens. One closed border rule. Double ring Popover diffuse projection. Shadows sm, md, lg, popover, composer shadow, hover, focus, and a Clay focus ring.

## Motion and interaction physics

- Time constants: 70ms tactile micro shake, 120ms geometry snap, 300ms panel slide in.
- 7 stop non linear Alpha fade mask, close to y equals x to the power 1.5, with dynamic zero at top and bottom contact.
- Button active scale 0.98. SegmentedControl 999px full radius track.
- ThinkingAccordion: CSS Grid 0fr to 1fr stretch. While generating, it stays forced open with a mutual exclusion lock.
- Approval card: three states, pending_approval, running, success, error.
- IME: CJK double interlock guard, with isComposing or keyCode 229, and Escape handling.
- Scroll: 40px bottom tolerance plus 150ms cooling timestamp to stop scroll stealing.

## Mapping to Android, plan, not yet implemented

- Port the tokens to Android native theme, color, and radius resources first. Do not put React components directly in a WebView as the first step.
- If React is reused in a WebView, open only a small set of strictly typed bridge interfaces. The web page does not get arbitrary command execution or file access.
- Port the approval card, the thinking fold, the model switcher, and the MCP decision card to native layout with the three states and the wording above, then adapt for narrow screens.
