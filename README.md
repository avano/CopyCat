# 🐈 CopyCat

A small tool powered by Quarkus, Hibernate and SQLite intended for quickly sharing stuff from clipboard between multiple machines.

Zero dependencies, small memory footprint and a quick startup time thanks to the quarkus native executable.

## Configuration

All configuration options are located in the [application.properties](./src/main/resources/application.properties) file, together with their default values.

If you use token authentication, you must provide the `token` value at runtime. You can pass it as a standard Java system property using `-Dtoken=value`, or preferably create a `.env` file next to the copycat binary containing:
```bash
token=value
```

## Example usage

### Linux X11 using xclip

```bash
# Send the content of the clipboard to copycat
xclip -selection clipboard -o | curl -s -H "${AUTH}" --data-binary @- https://copycat.example/push

# Get the last snippet from copycat
curl -s -H "${AUTH}" https://copycat.example/peek | xclip -selection clipboard -t "$(curl -s -H "${AUTH}" https://copycat.example/type)"
```

### Linux Wayland using wl-clipboard
```bash
# Send the content of the clipboard to copycat
wl-paste -n | curl -s -H "${AUTH}" --data-binary @- https://copycat.example/push

# Get the last snippet from copycat
curl -s -H "${AUTH}" https://copycat.example/peek | wl-copy
```

## Building

To reduce the requirements, builds are done inside containers and the resulting binary is then extracted from the image. See the [build.sh](build.sh) script and its help for details.

To build the binary for `Ubuntu` / `Debian` / `Fedora` run:
```bash
./build.sh
```

For Alpine Linux, the binary must be statically linked (Alpine uses musl), so build with:
```bash
./build.sh --static
```

If you prefer not to use containers, you can build as a standard Java Maven project with the Quarkus Maven plugin instead. Follow the prerequisites and environment setup described in [Quarkus docs](https://quarkus.io/guides/building-native-image).

Then run:

```bash
mvn clean package -DskipTests -Dnative
```
