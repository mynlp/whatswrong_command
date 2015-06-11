# whatswrong command

whatswrong command is a command line tool for whatswrong.

## Requirements

- JDK (>= 1.7)
- maven


## Dependencies

- org.riedelcastro.whatswrong (0.2.4)
- args4j.args4j (2.32)
- com.fasterxml.jackson.core.jackson-core (2.5.3)
- com.fasterxml.jackson.core.jackson-annotations (2.5.3)
- com.fasterxml.jackson.core.jackson-databind (2.5.3)
- org.apache.velocity.velocity (1.7)

Maven automatically download and install all these dependent libraries.
So, you don't need to install them manually.   


## Build

Use maven to compile.

```
$ mvn assembly:assembly
```

This command generates `target/whatswrong_command-x.x.x.jar` and `target/whatswrong_command-x.x.x-jar-with-dependencies.jar`.
`x.x.x` is version number. 
The former jar only contains the classes of this command. You need to set classpath to dependent libraries.
The other contains all the classes including dependent libraries, so you don't need to set the classpath. 


# Usage

```
java -jar whatswrong_command-x.x.x-jar-with-dependencies.jar DATA [GOLD] [--config FILE] [--output FILE] --type
``` 

Opiotns:
 DATA                                   : Path to data to show
 GOLD                                   : Path to gold data to compare
 --config FILE                          : Config file path (use default config
                                          if not specified)
 --output FILE                          : Path to output directory (default:
                                          output)
 --type [CONLL2000 | CONLL2002 |        : Select data type
 CONLL2003 | CONLL2004 | CONLL2005 |
 CONLL2006 | CONLL2008 | CONLL2009 |
 SEXPR]


This is a directory structure of a result.
When you specify only DATA, the result contains all images of results.
When you specify both DATA and GOLD, the result shows difference between the two data.
Set `--outpout` option to change the path to the output directory.

```
[OUTPUT_DIR]
 +- result.html
 +- image
     +- image_0.png
     +- image_1.png
     | ...
     +- image_n.png
```

You can change output format of result with `--config` option.
This is the default configuration:

```
{
  "format": {
	"anti_aliasing": true,
	"curved": true,
	"height_factor": null,
	"margin": null,
	"edge_type_color": {
	  "dep:FP": "#FF0000",
	  "dep:FN": "#0000FF"
	}
  },
  "filter": {
    "edge_type": ["dep", "role", "sense", "ner", "chunk", "pos", "align"],
    "edge_type_postfix": ["FP", "FN"],
	"token_forbidden_property": ["Pos", "Feats"]
  }
}
```

- `anti_aliasing`: Enable anti aliasing if true
- `curved`: Use curved array if true
- `height_factor`: Height of arrays. Use default value if it is `null`
- `margin`: Margin of tokens. Use default value if it is `null`
- `edge_type_color`: Mapping from edge labels to colors.
- `edge_type`: Edge types to show.
- `edge_type_postfix`: Postfix types of edges to show. Show all edges if it is `null`. Select from "Match", "FN", and "FP".
- `token_forbidden_property`: Properties specified here are hidden.

Check a document of whatswrong to check the details of the configuration. 
 