package com.zamnadev.mwhatsapp.Notificaciones;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Mensajes {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAwXAJAH8:APA91bH_oBpXFX-dgYkBwsgZnIXF82GSNW9KkDUBmTd6tfnL7DzJF7Rc1CaI_Dbpygab6rpuA9cOKEpQu_k5P2Rui7Lqo5guHFFIxrWKbk085n3jOp5CpIkkEerqEdayjFb6OyjwCI2t"
            }
    )

    @POST("fcm/send")
    Call<Exito> enviarNotificacion (@Body Enviar body);

}
