package com.example.examen_segundo_parcial_grupo_5.Configuracion;

public class api {
    public static final String separador = "/";
    public static final String ipadress = "192.168.0.7";
    public static final String RestApi = "PM01";
    public static final String PostRouting = "CreateContactos.php";
    public static final String GetRouting = "Listcontactos.php";
    public static final String DelRouting = "eliminarCont.php";
    public static final String PutRouting = "EditContacto.php";
    public static final String  EndpointPost = "http://"  + ipadress + separador + RestApi + separador + PostRouting;
    public static final String  EndpointGet = "http://"  + ipadress + separador + RestApi + separador + GetRouting;
    public static final String  EndpointDelt = "http://"  + ipadress + separador + RestApi + separador + DelRouting;
    public static final String  EndpointPut = "http://"  + ipadress + separador + RestApi + separador + PutRouting;

}
