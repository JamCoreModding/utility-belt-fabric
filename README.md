# FabricTemplateMod

This is the (opinionated) template used by JamCore to create their mods. The 
repo contains an MIT license file for ease of use, as that's what our mods use,
but you are free to use the template without the license.

To automatically initialise the template, use [Deno](https://deno.land/),
and run the command `deno run -A --unstable scripts/template.ts`.

To see available properties, see `gradle.properties`. If the comments in that
file reference adding a secret, you must add it either to the `local.properties`
file, or as an environment variable (with the name in upper case).
