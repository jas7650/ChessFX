package chess.engine.move;

public enum MoveStatus {
    DONE{
        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public boolean leavesInCheck() {
            return false;
        }

        @Override
        public boolean illegalMove() {
            return false;
        }
    },
    ILLEGAL_MOVE{
        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean leavesInCheck() {
            return false;
        }

        @Override
        public boolean illegalMove() {
            return true;
        }
    },
    LEAVES_PLAYER_IN_CHECK{
        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean leavesInCheck() {
            return true;
        }

        @Override
        public boolean illegalMove() {
            return false;
        }
    };

    public abstract boolean isDone();

    public abstract boolean leavesInCheck();

    public abstract boolean illegalMove();
}
