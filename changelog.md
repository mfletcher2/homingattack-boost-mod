- Add option to keep momentum after a Homing Attack (set `Homing Attack horizontal knockback` in mod options to `0`)
- Fix homing attack going through enemies without hitting
- Allow targeting enemies without directly looking at them. This will target the closest living entity that is in front of you. (set `Homing Attack angle range` to `0` for original behavior)
- Add homing attack on jump keybind
- Scale Homing Attack damage with held item
- Add options to disable animations and change reticle types
- Improve config screen layout
- Fix reticle beep when looking at non-living entities
- Add option to toggle boost (instead of holding the key)
- Add knockback to homing attack target
- Add option to disable boost on water
- Add cooldown for homing attack (1 second by default)
- Change boost effect duration to infinite (compatibility with ExtraSounds)
- Check whether homing & boost are enabled on server side
- Use injections in mixins, not overrides (for mod compatibility)

**Full Changelog**: https://github.com/mfletcher2/homingattack-boost-mod/compare/1.20.1-1.6a...1.20.1-1.7