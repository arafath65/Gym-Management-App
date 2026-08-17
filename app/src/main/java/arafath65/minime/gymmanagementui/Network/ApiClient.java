package arafath65.minime.gymmanagementui.Network;

import arafath65.minime.gymmanagementui.Utilss.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;


    // =====================================================
    // RETROFIT CLIENT
    // =====================================================

    public static Retrofit getClient() {

        if (retrofit == null) {

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(
                                    Constants.BASE_URL
                            )
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        return retrofit;
    }


    // =====================================================
    // API SERVICE
    // =====================================================

    public static ApiService getApiService() {

        return getClient()
                .create(
                        ApiService.class
                );
    }
}