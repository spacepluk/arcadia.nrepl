# arcadia.nrepl
This is an [nREPL] bridge for [Arcadia Unity].  It spins up a standard nREPL
server and it redirects eval operations to the Arcadia UDP REPL.  This will
allow to use existing tooling and editor integrations without the need to port
nREPL and other middlewares/libraries to ClojureCLR.

[nREPL]: https://github.com/clojure/tools.nrepl
[Arcadia Unity]: https://github.com/arcadia-unity/Arcadia

At the moment though, it's just a very dirty hack to make vim-fireplace work
with Arcadia :)

## How to use
- Download the jar from [releases](https://github.com/spacepluk/arcadia.nrepl/releases)
- Create an `.nrepl-port` file in your project
- Execute the jar with `java -jar`

```bash
wget https://github.com/spacepluk/arcadia.nrepl/releases/download/0.1.0-SNAPSHOT/arcadia.nrepl-0.1.0-SNAPSHOT-standalone.jar
echo 7888 > .nrepl-port
java -jar arcadia.nrepl-0.1.0-SNAPSHOT-standalone.jar
```

## vim-fireplace
You need to use [this fork](https://github.com/spacepluk/vim-fireplace) until
the patches are merged.  They shouldn't interfere with regular
Clojure/ClojureScript work.

## Other editors
I did some limited testing on other editors like Emacs and Atom+ProtoREPl and
at least basic eval operations seem to work correctly.

## Known-issues
- Autocomplete doesn't work due to the size limits of UDP packets.
- It's just nREPL, no CIDER yet.
- Emacs REPL buffer doesn't print the prompt for some reason, but it works.

