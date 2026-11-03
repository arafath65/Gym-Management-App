package arafath65.minime.gymmanagementui.Network;

import android.content.Context;

import arafath65.minime.gymmanagementui.Utilss.Constants;
import arafath65.minime.gymmanagementui.session.SessionStorage;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static Context applicationContext;

    private ApiClient() { }

    public static synchronized void initialize(Context context) {
        applicationContext = context.getApplicationContext();
        retrofit = null;
    }

    public static synchronized Retrofit getClient() {
        if (retrofit == null) {
            if (applicationContext == null) {
                throw new IllegalStateException(
                        "ApiClient is not initialized. Check GymManagementApplication.");
            }

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String token = new SessionStorage(applicationContext)
                                .getSessionToken();

                        Request.Builder requestBuilder = original.newBuilder();
                        if (token != null && !token.trim().isEmpty()) {
                            requestBuilder.header("X-User-Session", token);
                        }

                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
