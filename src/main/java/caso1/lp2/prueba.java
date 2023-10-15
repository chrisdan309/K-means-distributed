package caso1.lp2;

public class prueba {
    public static void main(String[] args) {
        int[] conjunto = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int numPartes = 3;
        int elementosPorParte = conjunto.length / numPartes;
        int elementosExtras = conjunto.length % numPartes;

        int indice = 0;

        for (int parte = 1; parte <= numPartes; parte++) {
            System.out.print("Parte " + parte + ": ");
            int elementosEnEstaParte = elementosPorParte;

            if (elementosExtras > 0) {
                elementosEnEstaParte++;
                elementosExtras--;
            }

            for (int i = 0; i < elementosEnEstaParte; i++) {
                if (indice < conjunto.length) {
                    System.out.print(conjunto[indice] + " ");
                    indice++;
                }
            }

            System.out.println();
        }
    }
}
