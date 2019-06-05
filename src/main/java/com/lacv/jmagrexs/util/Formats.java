package com.lacv.jmagrexs.util;

import java.math.BigInteger;
import java.sql.Time;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author lacastrillov@gmail.com
 *
 */
public class Formats {

    private static final Integer DIAS_ANYOS = 365;

    private static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
            + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
            + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
            + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" + "\u00C5\u00E5" + "\u00C7\u00E7"
            + "\u0150\u0151\u0170\u0171";
    private static final String PLAIN_ASCII = "AaEeIiOoUu" // grave
            + "AaEeIiOoUuYy" // acute
            + "AaEeIiOoUuYy" // circumflex
            + "AaOoNn" // tilde
            + "AaEeIiOoUuYy" // umlaut
            + "Aa" // ring
            + "Cc" // cedilla
            + "OoUu" // double acute
            ;
    
    public static final String[] TYPES= new String[]{"java.lang.String","char","java.lang.Character","short","java.lang.Short","int","java.lang.Integer","long","java.lang.Long",
                "java.math.BigInteger","double","java.lang.Double","float","java.lang.Float","boolean","java.lang.Boolean","java.util.Date","java.sql.Time"};
    
    public static final List TYPES_LIST= Arrays.asList(TYPES);
    
    public static final List FILE_EXTENSIONS= Arrays.asList(
                new String[] {"bat","conf","css","csv","class","html","java","jar","js","json","jsp","php","properties","log",
                    "sql","sh","txt","vm","war","xml","yml"});

    /**
     * Metodo que recupera la cantidad de dias entre dos fechas
     *
     * @param fechaInicio
     * @param fechaFin
     * @return String
     */
    public static String recuperarDiasFechas(Date fechaInicio, Date fechaFin) {
        String rst;
        GregorianCalendar calendarAhora = new GregorianCalendar();
        calendarAhora.setTime(fechaFin);
        GregorianCalendar calendarInicio = new GregorianCalendar();
        calendarInicio.setTime(fechaInicio);
        int rangoAnyos = calendarAhora.get(Calendar.YEAR) - calendarInicio.get(Calendar.YEAR);
        int totalDias = (rangoAnyos * DIAS_ANYOS) + (calendarAhora.get(Calendar.DAY_OF_YEAR) - calendarInicio.get(Calendar.DAY_OF_YEAR));

        rst = totalDias == 1 ? totalDias + " d&iacute;a" : totalDias > 30 ? "M&aacute;s de 30 d&iacute;as" : totalDias + " d&iacute;as";

        return rst;
    }

    /**
     * Metodo que compara si dos fechas son iguales
     *
     * @param fechaInicio
     * @param fechaFin
     * @return boolean
     */
    public static boolean fechasIguales(Date fechaInicio, Date fechaFin) {
        boolean rst = false;
        GregorianCalendar calendarAhora = new GregorianCalendar();
        calendarAhora.setTime(fechaFin);
        GregorianCalendar calendarInicio = new GregorianCalendar();
        calendarInicio.setTime(fechaInicio);
        int rangoAnyos = calendarAhora.get(Calendar.YEAR) - calendarInicio.get(Calendar.YEAR);
        int totalDias = (rangoAnyos * DIAS_ANYOS) + (calendarAhora.get(Calendar.DAY_OF_YEAR) - calendarInicio.get(Calendar.DAY_OF_YEAR));
        if (totalDias == 0) {
            return true;
        }

        return rst;
    }

    /**
     * Metodo que normaliza un texto para generar un alias
     *
     * @param nombre
     * @return
     */
    public static String recuperarAliasNombre(String nombre) {

        if (nombre != null) {
            nombre = nombre.toUpperCase();
            nombre = nombre.trim();
            nombre = nombre.replaceAll(" ", "_").replaceAll(",", "").replaceAll("[.]", "").replaceAll(":", "").replaceAll("=", "");

            nombre = eliminarCaracteresEspeciales(nombre);
            return convertNonAscii(nombre);
        } else {
            return "";
        }
    }

    /**
     * Metodo que recupera un long de un string
     *
     * @param cadena
     * @return
     */
    public static Integer recuperarNumeroString(String cadena) {
        Pattern numerosPattern = Pattern.compile("\\d+");
        Integer nro = 0;
        String rst = "";
        if (cadena != null) {
            Matcher m = numerosPattern.matcher(cadena);
            while (m.find()) {
                rst += m.group();
            }
            if (!rst.equals("")) {
                nro += Integer.parseInt(rst);
            }

        }
        return nro;
    }

    /**
     * Metodo que recupera un int de un string
     *
     * @param cadena
     * @return
     */
    public static int recuperarNumeroStringToInt(String cadena) {
        Pattern numerosPattern = Pattern.compile("\\d+");
        int nro = 0;
        String rst = "";
        if (cadena != null) {
            Matcher m = numerosPattern.matcher(cadena);
            while (m.find()) {
                rst += m.group();
            }
            if (!rst.equals("")) {
                nro += Integer.parseInt(rst);
            }

        }
        return nro;
    }

    /**
     * Metodo que valida si el contenido de una cadena es numerico
     *
     * @param cadena
     * @return
     */
    public static boolean esNumero(String cadena) {
        Pattern numerosPattern = Pattern.compile("\\d+");
        boolean rst = false;
        if (cadena != null) {
            if (!cadena.equals("")) {
                Matcher m = numerosPattern.matcher(cadena);
                if (m.matches()) {
                    rst = true;
                }
            }
        }
        return rst;
    }

    /**
     * Metodo para convertir a codigo ascci
     *
     * @param s
     * @return
     */
    public static String convertNonAscii(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                sb.append(PLAIN_ASCII.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * 
     * @param line
     * @return 
     */
    public static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    /**
     * Metodo para recuperar el caracter valido de un caracter codificado
     *
     * @param cadena
     * @return
     */
    public static String decodeHtmlISO88591(String cadena) {
        String rst = cadena.replaceAll("&Aacute;", "\u00C1").replaceAll("&aacute;", "\u00C1").replaceAll("&Aacute;", "\u00E1")
                .replaceAll("&Eacute;", "\u00C9").replaceAll("&eacute;", "\u00E9").replaceAll("&Iacute;", "\u00CD").replaceAll("&iacute;", "\u00ED")
                .replaceAll("&Oacute;", "\u00D3").replaceAll("&oacute;", "\u00F3").replaceAll("&Uacute;", "\u00DA").replaceAll("&uacute;", "\u00FA")
                .replaceAll("&Ntilde;", "\u00D1").replaceAll("&ntilde;", "\u00F1");

        return rst;
    }

    /**
     * Metdo para eliminar caracteres especiales de una cadena
     *
     * @param cadena
     * @return
     */
    public static String eliminarCaracteresEspeciales(String cadena) {
        String rst = cadena.replaceAll("\\?", "").replaceAll("\\$", "").replaceAll("\\%", "").replaceAll("\\#", "").replaceAll("\\~", "")
                .replaceAll("/", "").replaceAll("|", "").replaceAll("'", "");
        return rst;
    }
    
    /**
     * 
     * @param s
     * @return 
     */
    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    /**
     * Metodo que genera textos aleatorios
     *
     * @param size
     * @return
     */
    public static String generateRandomCode(int size) {
        String rst = "";
        int letras[][] = new int[3][2];
        letras[0][0] = 97;
        letras[0][1] = 122;
        letras[1][0] = 65;
        letras[1][1] = 90;
        letras[2][0] = 48;
        letras[2][1] = 57;
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            int fila = r.nextInt(letras.length);
            rst += (char) ((int) (Math.random() * (letras[fila][1] - letras[fila][0])) + letras[fila][0]);
        }
        return rst;
    }
    
    /**
     * Metodo que convierte un Date en String
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static String dateToString(Date date, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    
    /**
     * Metodo que convierte un Time en String
     *
     * @param time
     * @param format
     * @return
     * @throws ParseException
     */
    public static String timeToString(Time time, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(time);
    }

    /**
     * Metodo que convierte un String en Date
     *
     * @param strDate
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strDate, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(strDate);

        return date;
    }
    
    /**
     * 
     * @param value
     * @return 
     */
    public static Date stringToDate(String value){
        String dateValue= value.replaceAll("/", "-");
        String[] inFormats= new String[]{
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy hh:mm:ss a",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss'.000-05:00'"
        };
        for (String inFormat : inFormats) {
            Date date= validDateFormat(inFormat, dateValue);
            if(date!=null){
                return date;
            }
        }
        try{
            long dateLong= Long.parseLong(value);
            return new Date(dateLong);
        }catch(NumberFormatException ex){
            return null;
        }
    }
    
    /**
     * 
     * @param value
     * @return 
     */
    public static Time stringToTime(String value){
        String time= value;
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
        String[] inFormats= new String[]{
            "hh:mm:ss a",
            "hh:mm a",
            "h:mm a",
            "yyyy-MM-dd'T'HH:mm:ss"};

        for (String inFormat : inFormats) {
            Date current= validDateFormat(inFormat, value);
            if(current!=null){
                time= displayFormat.format(current);
                break;
            }
        }
        return Time.valueOf(time);
    }
    
    /**
     * 
     * @param value
     * @return 
     */
    public static Boolean stringToBoolean(String value){
        boolean returnValue = false;
        if ("true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value) || 
                "yes".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value)){
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Metodo que valida si un numero es de tipo Integer
     *
     * @param s
     * @return
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 
     * @param format
     * @param value
     * @return 
     */
    public static Date validDateFormat(String format, String value) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (value.equals(sdf.format(date))){
                return date;
            }
        } catch (ParseException ex) {
        }
        return null;
    }

    /**
     * Cast to
     *
     * @param type
     * @param value
     * @return
     * @throws java.lang.ClassNotFoundException
     */
    public static Object castParameter(String type, String value) throws ClassNotFoundException, NumberFormatException {
        if(!value.equals("")){
            switch (type) {
                case "java.lang.String":
                case "java.lang.Object":
                    return value;
                case "short":
                    return Short.parseShort(value);
                case "java.lang.Short":
                    return new Short(value);
                case "int":
                    return Integer.parseInt(value);
                case "java.lang.Integer":
                    return new Integer(value);
                case "long":
                    return Long.parseLong(value);
                case "java.lang.Long":
                    return new Long(value);
                case "boolean":
                case "java.lang.Boolean":
                    return stringToBoolean(value);
                case "java.util.Date":
                    return stringToDate(value);
                case "java.sql.Time":
                    return stringToTime(value);
                case "java.math.BigInteger":
                    return new BigInteger(value);
                case "double":
                    return Double.parseDouble(value);
                case "java.lang.Double":
                    return new Double(value);
                case "float":
                    return Float.parseFloat(value);
                case "java.lang.Float":
                    return new Float(value);
                case "char":
                case "java.lang.Character":
                    return value.charAt(0);
                case "java.lang.Class":
                    return Class.forName(value);
                default:
                    break;
            }
        }
        return null;
    }
    
    /**
     * Cast to
     *
     * @param type
     * @return
     * @throws java.lang.ClassNotFoundException
     */
    public static String getDatabaseType(String type) throws ClassNotFoundException, NumberFormatException {
        switch (type) {
            case "java.lang.String":
                return "VARCHAR";
            case "char":
            case "java.lang.Character":
                return "CHAR";
            case "short":
            case "java.lang.Short":
                return "SMALLINT";
            case "int":
            case "java.lang.Integer":
                return "INT";
            case "long":
            case "java.lang.Long":
            case "java.math.BigInteger":
                return "BIGINT";
            case "double":
            case "java.lang.Double":
                return "DOUBLE";
            case "float":
            case "java.lang.Float":
                return "FLOAT";
            case "boolean":
            case "java.lang.Boolean":
                return "TINYINT";
            case "java.util.Date":
                return "DATETIME";
            case "java.sql.Time":
                return "TIME";
            default:
                return "VARCHAR";
        }
    }
    
    /**
     * 
     * @param contentType
     * @return 
     */
    public static String getSimpleContentType(String contentType){
        if(FILE_EXTENSIONS.contains(contentType.toLowerCase())){
            return contentType.toLowerCase();
        }
        switch(contentType){
            case "image/gif":
            case "image/png":
            case "image/jpeg":
                return "image";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            case "application/vnd.oasis.opendocument.text":
            case "application/msword":
                return "doc";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            case "application/vnd.ms-excel":
                return "xls";
            case "application/pdf":
                return "pdf";
            case "7z":
            case "rar":
            case "application/zip":
            case "application/gzip":
            case "application/x-tar":
            case "application/x-rar":
            case "application/x-gzip":
            case "application/x-zip-compressed":
                return "compress";
            case "text/x-java":
                return "java";
            case "application/x-java-archive":
                return "jar";
            case "application/x-webarchive":
                return "war";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
            case "application/vnd.oasis.opendocument.presentation":
            case "application/vnd.ms-powerpoint":
                return "ppt";
            case "audio/mpeg":
                return "audio";
            case "video/mp4":
                return "video";
            case "application/javascript":
                return "js";
            case "text/css":
                return "css";
            case "text/plain":
                return "txt";
            case "text/html":
                return "html";
            case "application/x-php":
                return "php";
            case "text/xml":
                return "xml";
            case "text/x-log":
                return "log";
            case "application/json":
                return "json";
            default:
                return "file";
        }
    }

}
