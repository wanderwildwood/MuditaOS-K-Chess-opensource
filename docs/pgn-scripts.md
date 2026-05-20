# PGN Scripts

## Loading advanced chess board position to application

1. Prepare a board position in PGN using for example https://www.chess.com/analysis?tab=analysis.
2. Copy prepared PGN to file scripts/game.pgn
3. Run script [./scripts/loadGame.sh](../scripts/loadGame.sh).
   Additionally you can specify player side color (second argument) and difficulty level (third argument)
   like `./scripts/loadGame.sh black 3`

To clear loaded game run script [./scripts/purneGame.sh](../scripts/purneGame.sh).

Full description is available
on [Confluence](https://appnroll.atlassian.net/wiki/spaces/MiquidoChess/pages/2325446659/Loading+advanced+chess+board+position+to+application)

## Pulling chess board position from application

1. Run script [./scripts/pullGame.sh](../scripts/pullGame.sh).
2. Observe in terminal content of Saved Games table from app database

Full description is available
on [Confluence](https://appnroll.atlassian.net/wiki/spaces/MiquidoChess/pages/2342158406/Pulling+chess+board+position+from+application)
