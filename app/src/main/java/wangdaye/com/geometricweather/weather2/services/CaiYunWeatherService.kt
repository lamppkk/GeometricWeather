package wangdaye.com.geometricweather.weather2.services

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import wangdaye.com.geometricweather.common.basic.models.ChineseCity.CNWeatherSource
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.weather2.apis.CNWeatherApi
import wangdaye.com.geometricweather.weather2.apis.CaiYunApi
import wangdaye.com.geometricweather.weather2.converters.CaiyunResultConverter
import javax.inject.Inject

/**
 * CaiYun weather service.
 */
class CaiYunWeatherService @Inject constructor(private val api: CaiYunApi,
                                               cnApi: CNWeatherApi) : CNWeatherService(cnApi) {

    override val source: CNWeatherSource
        get() = CNWeatherSource.CAIYUN

    override suspend fun getWeather(context: Context, location: Location): Weather? {
        return withContext(Dispatchers.IO) {
            val mainly = async {
                api.getMainlyWeather(location.latitude.toString(), location.longitude.toString(),
                        location.isCurrentPosition,
                        "weathercn%3A" + location.cityId,
                        15,
                        "weather20151024",
                        "zUFJoAR2ZVrDy1vF3D07",
                        "V10.0.1.0.OAACNFH",
                        "10010002",
                        false,
                        false,
                        "gemini",
                        "",
                        "zh_cn"
                )
            }
            val forecast = async {
                api.getForecastWeather(location.latitude.toString(), location.longitude.toString(),
                        "zh_cn",
                        false,
                        "weather20151024",
                        "weathercn%3A" + location.cityId,
                        "zUFJoAR2ZVrDy1vF3D07"
                )
            }

            return@withContext CaiyunResultConverter.convert(
                    context, location, mainly.await(), forecast.await())
        }.result
    }
}