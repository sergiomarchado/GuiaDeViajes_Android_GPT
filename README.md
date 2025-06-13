# PET EXPLORER!!! 🗺️ Guía de Viajes Pet-Friendly 🐶 (Android) con IA

Una aplicación Android moderna que combina Jetpack Compose con Google Maps y la API de ChatGPT para ayudarte a encontrar lugares pet-friendly.
Ofrece búsquedas geolocalizadas, filtros inteligentes, caché local y visualización enriquecida de resultados.

---

## 🚀 Características

* 🤖 Integración con la API de Inteligencia Artificial de OpenAI ChatGPT para dar formato markdown a una respuesta con las recomendaciones inteligentes.
* 🌍 Mapa interactivo con Google Maps y estilo personalizado
* 📍 Búsqueda por ciudad o ubicación textual (Geocoding API)
* 🔎 Resultados detallados: dirección, teléfono, web y fotos
* 💾 Caché local con Room para minimizar llamadas a la API y reducir costes
* 📷 Carga dinámica de imágenes desde Google Places
* 📍 Apertura directa del lugar en Google Maps
* 🧭 Navegación con filtros personalizables
* 🧠 Recomendaciones enriquecidas mediante IA

---

## 🎬 Demo en vídeo

[![Demo de la app](https://img.youtube.com/vi/MD548YvtCis/0.jpg)](https://youtu.be/MD548YvtCis)

---

## 🛠️ Tecnologías utilizadas

| Categoría    | Tecnología                          |
| ------------ | ----------------------------------- |
| Lenguaje     | Kotlin                              |
| Arquitectura | MVVM + StateFlow                    |
| UI           | Jetpack Compose + Material 3        |
| Inyección    | Hilt / Dagger                       |
| Navegación   | Navigation Compose                  |
| Google APIs  | Maps SDK, Places API, Geocoding API |
| Red          | Retrofit + OkHttp                   |
| Cache local  | Room                                |
| Imágenes     | Coil v3                             |
| AI           | ChatGPT API (OpenAI)                |
| Testing      | JUnit, Espresso                     |

---

## 🔐 Variables de entorno

Para que la App funcione, se deberá contar con Api Keys propias de las Apis de Google y OpenAI.
Las claves de API se almacenan en `local.properties`:

Mira más detalles sobre esto en la parte de instalación.

```properties
API_KEY=tu_api_key_geocoding
API_KEYG=tu_api_key_places_maps
```

Estas se inyectan mediante `build.gradle.kts`:

```kotlin
buildConfigField("String", "API_KEY", "\"$apiKey\"")
buildConfigField("String", "API_KEYG", "\"$apiKeyG\"")
```

---

## ⚙️ Estructura del proyecto

```text
com.example.guiadeviajes_android_gpt
├── auth                      # Lógica de autenticación (si aplica)
├── home                      # Pantalla principal
│   ├── data                  # Repositorios y APIs (Google, OpenAI)
│   │   ├── remote
│   │   │   └── dto           # Modelos de red (Google, ChatGPT)
│   │   └── repository        # Repositorios de datos
│   ├── di                    # Módulos de Hilt (Retrofit, etc)
│   └── presentation          # Componentes UI + navegación
├── map
│   ├── core                  # Base de datos Room
│   ├── dto                   # Modelo de dominio (PetInterest)
│   ├── presentation          # Pantallas de mapa
│   ├── util                  # Utilidades de colores y mapas
│   └── navigation            # Barra inferior del mapa
├── profile                   # Gestión del perfil del usuario
├── promotions                # Promociones y ofertas
├── results                   # Resultados guardados y detalles
├── ui.theme                  # Tema global (Material 3)
├── MainActivity.kt
└── MyApp.kt
```

---

## 🧠 Lógica interna

### 🏠 Pantalla Home: búsqueda inteligente con IA

Desde la pantalla de inicio, los usuarios pueden seleccionar una ciudad, un país y uno o más intereses pet-friendly. Internamente:

1. Se llama a varias APIs de Google (FindPlace, Text Search, Nearby Search, Geocoding) para obtener resultados precisos.
2. Se agregan, filtran y transforman los resultados en un resumen limpio.
3. Este resumen se envía a la API de ChatGPT con instrucciones para devolver una respuesta en **Markdown**.
4. La respuesta se renderiza como HTML en un `WebView` personalizado.

```kotlin
val prompt = "Genera una guía pet-friendly con estos lugares..."
val markdown = chatgptApi.generateMarkdown(prompt)
```

Este enfoque permite mostrar contenido enriquecido, con contexto, estilo y lenguaje natural.

### 🗺️ Pantalla Mapa: exploración dinámica y filtrada

Desde la vista de mapa:

* Se permite buscar por ciudad (con `geocodeAndFetch`) para centrar el mapa.
* El usuario puede aplicar filtros (veterinarios, parques, restaurantes...) y pulsar "Buscar en esta zona".
* Se consulta primero Room (caché local) y, si no hay datos, se lanza una Nearby Search limitada por categoría.
* Se limita a 5 resultados por categoría y se actualiza `_places` en el ViewModel.

```kotlin
fun fetchPlaces(center: LatLng, radius: Int = 1500)
```

Esto evita múltiples llamadas a la API innecesarias, reduce costes y mejora la velocidad de carga.

---

## 📦 Instalación

1. Clona el repositorio:

```bash
git clone https://github.com/tu-usuario/tu-repo.git
```

2. Crea un archivo `local.properties` y crea las siguientes variables, con las keys de las apis que tendrás que tener OBLIGATORIAMENTE, tanto de Google APIs (Maps SDK, Places API, Geocoding API) como de OpenAI API. La que está marcada con la G (API_KEYG) será la de Google:

```properties
API_KEY=...
API_KEYG=...
```

3. Abre el proyecto en Android Studio y ejecútalo ✅

---

## ✅ TODOs

* Soporte offline real
* Agregar favoritos
* Intereses recomendados por IA (ChatGPT)
* Modo oscuro automático
* Localización multilenguaje

---

## 📄 Licencia

MIT © \[Tu Nombre o Alias]
