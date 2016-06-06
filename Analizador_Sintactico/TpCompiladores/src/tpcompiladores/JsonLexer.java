/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Usuario
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpcompiladores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import static java.lang.Character.isDigit;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Acer Aspire
 */
public class JsonLexer {

    public static BufferedReader2 archivo;
    public static int nroLinea = 1;
    public static String literal = "";
    public static Registro token = null;
    public static LinkedList<Registro> lista = new LinkedList<Registro>();
    public static Registro[] reservedWord = new Registro[3];//solo hay 3 palabras reservadas
    public static FileWriter fwriter = null;
    public static PrintWriter pw = null;
    public static FileReader freader = null;
    public static final int L_CORCHETE = 1;
    public static final int R_CORCHETE = 2;
    public static final int L_LLAVE = 3;
    public static final int R_LLAVE = 4;
    public static final int COMA = 5;
    public static final int DOS_PUNTOS = 6;
    public static final int LITERAL_CADENA = 7;
    public static final int LITERAL_NUM = 8;
    public static final int PR_TRUE = 9;
    public static final int PR_FALSE = 10;
    public static final int PR_NULL = 11;
    public static final int EOF = 12;
    public static final int ERROR = -1;

    public static void main(String[] args) {
        initReservedWodrd();
        Scanner teclado = new Scanner(System.in);
        File path = null;

        do {
            System.out.print("Ingrese la ruta del archivo: ");
            path = new File(teclado.nextLine());
        } while (!path.canRead());

        try {
            freader = new FileReader(path);
            archivo = new BufferedReader2(freader);
            do {
                sigLex();
            } while (!("EOF".equals(lista.get(lista.size() - 1).comLex)));
            fwriter = new FileWriter(path.getParent() + "\\output.txt");
            pw = new PrintWriter(fwriter);
            for (Registro r : lista) {
                pw.print(r.comLex + "  ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fwriter != null) {
                    fwriter.close();
                }
                if (freader != null) {
                    freader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static LinkedList initLexer() {
        Scanner teclado = new Scanner(System.in);
        FileReader freader = null;
        File path = null;

        do {
            System.out.print("Ingrese la ruta del archivo: ");
            path = new File(teclado.nextLine());
        } while (!path.canRead());

        try {
            freader = new FileReader(path);
            archivo = new BufferedReader2(freader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void cerrarArchivo() {
        try {
            if (fwriter != null) {
                fwriter.close();
            }
            if (freader != null) {
                freader.close();
            }
            if (archivo != null) {
                archivo.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void sigLex() throws IOException {
        char caracter;
        while ((caracter = archivo.getchar()) != (char) -1) {
            literal= "{";
            if (caracter == ' ' || caracter == '\t' || caracter == '\r') {
                continue;
            } else if (caracter == '\n') {
                nroLinea++;
                continue;
            } else if (caracter == '"') {//empieza reconocimiento de String
                int estado = 1;
                boolean acepto = false;
                literal = literal + caracter;
                while (!acepto) {
                    switch (estado) {
                        case (1):
                            caracter = archivo.getchar();
                            if (caracter == '\\') {
                                literal = literal + caracter;
                                estado = 2;
                            } else if (caracter == '"') {
                                literal = literal + caracter;
                                estado = 3;
                            } else if (caracter == '\n') {
                                estado = -1;
                            } else if (caracter == (char) -1) {
                                estado = -1;
                            } else {
                                literal = literal + caracter;
                                estado = 1;
                            }
                            break;
                        case (2):
                            caracter = archivo.getchar();
                            if (caracter == '"') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == 'n') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == 't') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == 'f') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == 'b') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == 'r') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == '\\') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == '/') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (caracter == 'u') {
                                literal = literal + caracter;
                                estado = 1;
                            } else {
                                estado = -2;
                            }
                            break;
                        case (3):
                            acepto = true;
                            lista.add(token = new Registro("LITERAL_CADENA", literal, 7));
                            break;
                        case (-1):
                            if (caracter == '\n') {
                                archivo.ungetchar();//devolver para contar numero de linea
                            }
                            error("literal de cadena sin cerrar");
                            return;//salimos debido al error
                        case (-2):
                            error("caracter de escape no valido");
                            char c = caracter;
                            while (c != '\n' && c != (char) -1) {
                                c = archivo.getchar();
                            }
                            archivo.ungetchar();//devolver el enter o el eof
                            return;//salimos debido al error
                    }

                }
                break;//sale de while((caracter = archivo.getchar()) != (char)-1)
            } else if (caracter == ':') {
                lista.add(token = new Registro("DOS_PUNTOS", ":", 6));
                break;
            } else if (caracter == '[') {
                lista.add(token = new Registro("L_CORCHETE", "[", 1));
                break;
            } else if (caracter == ']') {
                lista.add(token = new Registro("R_CORCHETE", "]", 2));
                break;
            } else if (caracter == '{') {
                lista.add(token = new Registro("L_LLAVE", "{", 3));
                break;
            } else if (caracter == '}') {
                lista.add(token = new Registro("R_LLAVE", "}", 4));
                break;
            } else if (caracter == ',') {
                lista.add(token = new Registro("COMA", ",", 5));
                break;
            } else if (Character.isLetter(caracter)) {
                do {
                    literal = literal + caracter;
                    caracter = archivo.getchar();
                } while (Character.isLetter(caracter));//repetir mientras sea letra
                archivo.ungetchar();
                for (Registro word : reservedWord) {
                    if (word.lexema.equals(literal) || word.lexema.toLowerCase().equals(literal)) {
                        lista.add(token = new Registro(word.comLex, literal, word.id));
                        return;
                    }
                }
                error("lexema no valido " + literal);
                return;
            } else if (isDigit(caracter)) {
                //es un numero 
                int i = 0;
                int estado = 0;
                boolean acepto = false;
                literal = literal + caracter;

                while (!acepto) {

                    switch (estado) {
                        case 0: //una secuencia netamente de digitos, puede ocurrir . o e 
                            caracter = archivo.getchar();
                            if (isDigit(caracter)) {
                                literal = literal + caracter;
                                estado = 0;
                            } else if (caracter == '.') {
                                literal = literal + caracter;
                                estado = 1;
                            } else if (Character.toLowerCase(caracter) == 'e') {
                                literal = literal + caracter;
                                estado = 3;
                            } else {
                                estado = 6;
                            }
                            break;

                        case 1://un punto, debe seguir un digito
                            caracter = archivo.getchar();
                            if (isDigit(caracter)) {
                                literal = literal + caracter;
                                estado = 2;
                            } else {
                                error("no se esperaba " + caracter);
                                estado = -1;
                            }
                            //no se si podria venir una 'e'
                            break;
                        case 2://la fraccion decimal, pueden seguir los digitos o e 
                            caracter = archivo.getchar();
                            if (isDigit(caracter)) {
                                literal = literal + caracter;
                                estado = 2;
                            } else if (Character.toLowerCase(caracter) == 'e') {
                                literal = literal + caracter;
                                estado = 3;
                            } else {
                                estado = 6;
                            }
                            break;
                        case 3://una e, puede seguir +, - o una secuencia de digitos 
                            caracter = archivo.getchar();
                            if (caracter == '+' || caracter == '-') {
                                literal = literal + caracter;
                                estado = 4;
                            } else if (isDigit(caracter)) {
                                literal = literal + caracter;
                                estado = 5;
                            } else {
                                error("no se esperaba " + caracter);
                                estado = -1;
                            }
                            break;
                        case 4://necesariamente debe venir por lo menos un digito 
                            caracter = archivo.getchar();
                            if (isDigit(caracter)) {
                                literal = literal + caracter;
                                estado = 5;
                            } else {
                                error("no se esperaba " + caracter);
                                estado = -1;
                            }
                            break;
                        case 5://una secuencia de digitos correspondiente al exponente 
                            caracter = archivo.getchar();
                            if (isDigit(caracter)) {
                                literal = literal + caracter;
                                estado = 5;
                            } else {
                                estado = 6;
                            }
                            break;
                        case 6://estado de aceptacion, devolver el caracter correspondiente a otro componente lexico 
                            if (caracter != (char) -1) {
                                archivo.ungetchar();
                            } else {
                                caracter = (char) 0;
                            }
                            acepto = true;
                            lista.add(token = new Registro("LITERAL_NUM", literal, 8));
                            break;
                        case -1:
                            if (caracter == (char) -1) {
                                error("no se esperaba fin de archivo");
                            } else if (caracter == '\n') {
                                archivo.ungetchar();//devolver para contar numero de linea
                                error("no se esperaba fin de linea");
                            }
                            return;
                    }
                }
            } else {
                error("caracter no valido " + caracter);
            }
        }
        if (caracter == (char) -1) {
            lista.add(token = new Registro("EOF", "eof", 12));
        }
    }

    public static void error(String msg) {
        System.out.println(String.format("Linea %-4d" + " " + msg, nroLinea));
        token = new Registro("ERROR", "ERROR", -1);
    }

    public static void initReservedWodrd() {
        reservedWord[0] = new Registro("PR_TRUE", "TRUE", 9);
        reservedWord[1] = new Registro("PR_FALSE", "FALSE", 10);
        reservedWord[2] = new Registro("PR_NULL", "NULL", 11);
    }
}

class BufferedReader2 extends BufferedReader {

    public BufferedReader2(Reader in) {
        super(in);
    }

    public char getchar() throws IOException {
        mark(1);
        return (char) this.read();
    }

    public void ungetchar() throws IOException {
        reset();
    }
}

class Registro {

    String comLex;
    String lexema;
    int id;

    public Registro(String comLex, String lexema, int id) {
        this.comLex = comLex;
        this.lexema = lexema;
        this.id = id;
    }

    @Override
    public String toString() {
        return "" + comLex + "  ";
    }
}
