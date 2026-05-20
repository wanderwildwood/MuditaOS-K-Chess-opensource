# Glossary

**UCI** (short for Universal Chess Interface) is the standard for graphical chess programs to communicate with chess engines.

**PGN** (short for Portable Game Notation) is the standard format for recording a game in a text file that is processible by computers.

Example: [game.pgn](../scripts/game.pgn)

**SAN** (short for Short Algebraic Notation) is notation where each move of a piece is indicated by the piece's uppercase letter,
plus the coordinates of the destination square.

For example, Be5 (bishop moves to e5), Nf3 (knight moves to f3).
For pawn moves, a letter indicating pawn is not used, only the destination square is given. For example, c5 (pawn moves to c5).

**LAN** (short for Long Algebraic Notation) is fully expanded algebraic notation, both the starting and ending squares are specified.
For example: e2e4, e1g1 (castling), e7e8q (promotion)
A form of long algebraic notation (without piece names) is also used by the UCI standard.

**FEN** (short for Forsyth-Edwards Notation) is the standard notation to describe positions of a chess game.
FEN differs from the PGN because it denotes only a single position instead of the moves that lead to it.
