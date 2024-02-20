package edu.escuelaing.arem.ASE.app;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    static Map<String, Method> data= new HashMap<>();

    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }

        boolean running=true;

        while (running){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            outputLine="";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if(inputLine.startsWith("GET")){
                    if(inputLine.contains("/calculadora")){
                        System.out.println("entro a calculadora");
                        outputLine=html();
                    }else if(inputLine.contains("/computar?comando")){
                        System.out.println("entro a computar");
                        String info= inputLine.split("=")[1];
                        String p=info.split(" ")[0].replace("(", " ").replace(")", "");
                        String fun=p.split(" ")[0];
                        String param=p.split(" ")[1];
                        Double paramDouble= Double.parseDouble(param);
                        //Method method= Math.class.getMethod(fun);
                       // System.out.println("method "+ method);
                        //execute(method, paramDouble);
                        System.out.println("p   "+ p);
                        System.out.println("fun " + fun);
                        System.out.println("param" + param);
                        outputLine=responseComputar();
                    }
                }
                if (!in.ready()){
                    break;
                }
            }
                out.println(outputLine);
                out.close();
                in.close();
                clientSocket.close();
            }
             serverSocket.close();
    }

    public static String html(){
        return "HTTP/1.1 200 OK\\r\\n"+
                "Content-Type: text/html\\r\\n"+
                "\\r\\n"+
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Form with GET</h1>\n" +
                "        <form action=\"/hello\">\n" +
                "            <label for=\"name\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/hello?name=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
                "        <h1>Form with POST</h1>\n" +
                "        <form action=\"/hellopost\">\n" +
                "            <label for=\"postname\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\n" +
                "        </form>\n" +
                "        \n" +
                "        <div id=\"postrespmsg\"></div>\n" +
                "        \n" +
                "        <script>\n" +
                "            function loadPostMsg(name){\n" +
                "                let url = \"/hellopost?name=\" + name.value;\n" +
                "\n" +
                "                fetch (url, {method: 'POST'})\n" +
                "                    .then(x => x.text())\n" +
                "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\n" +
                "            }\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";
    }

    public static void methodsMath(){
        Class c= Math.class;
        Method [] methods= c.getMethods();
        for(Method method: methods){
            Class [] params= method.getParameterTypes();
            for(Class param: params){
                if(param.getName().equals("double")){
                    data.put(method.getName(),method);
                }
            }
        }

    }

    public static String  execute(Method method, Double param) throws InvocationTargetException, IllegalAccessException {
        String response="";
        if(data.containsKey(method.getName())){
                response="response: "+method.invoke(null, param);
            System.out.println("invoke"+method.invoke(null, param));
        }else{
            response="no se encontro";
        }
        return response;
    }

    public static String responseComputar(){
        return "HTTP/1.1 200 OK\\r\\n"+
        "Content-Type: application/javascript\\r\\n"+
                "\\r\\n";
    }

}