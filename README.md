# PHP lexer

PHP Language lexer. Lab work at my University.

## Task

Write a lexer of a php language.

## Logic

The basic idea is that we match a list of regexes against the current line. If one of them matches, we store that token and advance our offset to the first character after the match. If no token is found and we are not yet at the end of the line, that means something invalid is in front of our offset.


## License
[MIT](https://choosealicense.com/licenses/mit/)
