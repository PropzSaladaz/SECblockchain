package pt.tecnico.blockchain;

//TO DO MAYBE A GOOD ABSTRACTION
public class SeqNumber {

        public static int seq = -1;

        public SeqNumber() {
            seq++;
        }

        public int getSeq() {
            return seq;
        }

}
