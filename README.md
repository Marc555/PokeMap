# PokeMap

## Índice

1. Descripción del proyecto
2. Objetivos de la App
3. Público Objetivo
4. Funcionalidades
   - Pantalla de Login / Registro
   - Pantalla de Home
   - Detalle de Categoría
   - Detalle de Ítem
   - Ranking
   - Búsqueda de Usuarios
   - Perfil de Usuario
   - Ajustes
   - Crash Analytics
5. Requisitos Funcionales por Rol
6. Arquitectura
7. Código

---

## 1. Descripción del Proyecto

**PokeMap** es una aplicación diseñada como un foro geolocalizado para jugadores de **Pokémon GO**. Los usuarios podrán compartir ubicaciones clave como nidos, gimnasios, spawns de legendarios y más. Incluye un sistema de seguidores, un buscador avanzado y filtros personalizados.

---

## 2. Objetivos de la App

- Facilitar la localización de puntos de interés de Pokémon GO.
- Personalizar la experiencia de búsqueda según intereses específicos (Pokémon, gimnasios, etc.).
- Fomentar la colaboración y la interacción entre jugadores.
- Proporcionar información actualizada y precisa.

---

## 3. Público Objetivo

- **Edad:** 13-40 años.
- **Perfil:** Jugadores activos de Pokémon GO.
- **Intereses:** Eventos, raids, capturas y colaboraciones en el juego.
- **Necesidad:** Descubrir ubicaciones relevantes y colaborar con otros jugadores.

---

## 4. Funcionalidades

### Pantalla de Login / Registro

- Login con usuario y contraseña.
- Registro con datos completos (usuario, contraseña, equipo, código de amigo).
- Recuperar contraseña.
- Login con Google.
- Login biométrico.

<img src="https://drive.google.com/uc?export=view&id=1mvHDOIXrrmjH2QvfsBSTCj0vT33CAU8i" width="200">

### Menu de Navegación

- Permite navegar entre las diferentes pantallas disponibles.
- Permite cerrar sesión

<img src="https://drive.google.com/uc?export=view&id=1FIuq7IZ-17UIPRXB8-IP1udyLqCzMoR8" width="200">

### Pantalla de Home

- Listado completo de categorías (nidos, gimnasios, spawns de legendarios, etc.).
- Filtros por nombre.
- Acceso al detalle de cada categoría.
- Creación, modificación y eliminación de categorías (solo admin).

<img src="https://drive.google.com/uc?export=view&id=1rca0z9lNdzPUxRK2rHhL5qWK0_eEqkzu" width="200">
<img src="https://drive.google.com/uc?export=view&id=16tdUCX10GoxOgyb_S3M9UN0CqEWXPnr7" width="200">

### Detalle de Categoría

- Vista completa de una categoría.
- Listado de ítems asociados.
- Filtros avanzados de ítems (nombre, distancia, valoración, etc.).
- Ordenar ítems por distancia, valoración o fecha.
- Creación de ítems.
- Modificación y eliminación de ítems (solo creador y admin).

<img src="https://drive.google.com/uc?export=view&id=16_B2iqCENrPqPV7nuwKYiLalEeVjvHUN" width="200">
<img src="https://drive.google.com/uc?export=view&id=16x7RUIhEolAzbzBatLtDJpD9AS7SEnLo" width="200">
<img src="https://drive.google.com/uc?export=view&id=1FL8QTJgHU8lLPhQW3mKwtdU0Jmb8h1Nn" width="200">

### Detalle de Ítems

- Al seleccionar un ítem, se muestra su información ampliada (nombre, descripción, valoración, comentarios, etc.).
- Mapa interactivo con la ubicación del ítem.
- Permite dejar una valoración del ítem.
- Permite dejar un comentario sobre el ítem.

<img src="https://drive.google.com/uc?export=view&id=1o0mRydipOtKtTZ3kKGsdJoHrLAbmas9R" width="200">

### Rankings

- Permite visualizar un ranking con los usuarios mejor valorados.
- Permite visualizar un ranking de los usuarios con más actividad.
- Permite visualizar un ranking de los usuarios con más seguidores.

<img src="https://drive.google.com/uc?export=view&id=1wX900rLMpRfVR8fsv-tR3NiGd-5Cmd-l" width="200">
<img src="https://drive.google.com/uc?export=view&id=1-Adya-cAtmUdLOKOUmfqtroYx5dto6TH" width="200">

### Búsqueda de Usuarios

- Permite buscar otros usuarios por su nombre de usuario.
- Permite visualizar la lista completa de usuarios (solo admin).

<img src="https://drive.google.com/uc?export=view&id=1kF1hLbUbmSKKYsBUSp_zn6inHBe0flzi" width="200">
<img src="https://drive.google.com/uc?export=view&id=1AKBQUFkj_XsnEfSy9_5L13E7VmY75IMl" width="200">

### Perfil de Usuario

- Permite visualizar la información del usuario seleccionado.
- Permite seguir o dejar de seguir al usuario.
- Permite visualizar las publicaciones y comentarios más destacados del usuario.
- Permite que el usuario actualice su información.

<img src="https://drive.google.com/uc?export=view&id=1wSDIgrASqLFSPmFAIzuaTWLf6_3wG10K" width="200">
<img src="https://drive.google.com/uc?export=view&id=ID_DE_LA_IMAGEN" width="200">

### Ajustes

- Permite configurar el idioma de la aplicación.
- Permite activar o desactivar la autenticación biométrica.
- Permite enviar un mensaje a los administradores (solo usuario).
- Permite leer los mensajes enviados por los usuarios (solo admin).

<img src="https://drive.google.com/uc?export=view&id=1IqLNs4_xURnaltq2QF874xsZfjgyg8Ck" width="200">

### Crash Analytics

- Registra los datos cada vez que la aplicación tiene un crash y los almacena en Firebase.

---

## 5. Requisitos Funcionales por Rol

### Administrador

- RF01 - Login y Logout.
- RF03 - Recuperar contraseña.
- RF04 - Crear nueva categoría.
- RF05 - Listar categorías.
- RF06 - Filtrar categorías.
- RF07 - Ampliar información de categoría.
- RF08 - Modificar categoría.
- RF09 - Eliminar categoría.
- RF10 - Crear nuevo ítem.
- RF11 - Listar ítems.
- RF12 - Filtrar ítems por categoría.
- RF13 - Filtrar ítems por nombre.
- RF14 - Filtrar ítems por distancia.
- RF15 - Filtrar ítems por valoración.
- RF16 - Ordenar ítems por valoración.
- RF17 - Ordenar ítems por distancia.
- RF18 - Ordenar ítems por fecha.
- RF19 - Ampliar información de un ítem.
- RF20 - Valorar ítems.
- RF21 - Visualizar valoraciones.
- RF22 - Modificar ítem.
- RF23 - Eliminar ítem.
- RF25 - Censurar comentarios.
- RF26 - Sistema de seguidores.
- RF27 - Ranking de usuarios.
- RF28 - Autenticación con Google.
- RF29 - Login biométrico.
- RF30 - Editar perfil de usuario.

### Usuario

- RF01 - Login y Logout.
- RF02 - Registro.
- RF03 - Recuperar contraseña.
- RF05 - Listar categorías.
- RF06 - Filtrar categorías.
- RF07 - Ampliar información de categoría.
- RF10 - Crear nuevo ítem.
- RF11 - Listar ítems.
- RF12 - Filtrar ítems por categoría.
- RF13 - Filtrar ítems por nombre.
- RF14 - Filtrar ítems por distancia.
- RF15 - Filtrar ítems por valoración.
- RF16 - Ordenar ítems por valoración.
- RF17 - Ordenar ítems por distancia.
- RF18 - Ordenar ítems por fecha.
- RF19 - Ampliar información de un ítem.
- RF20 - Valorar ítems.
- RF21 - Visualizar valoraciones.
- RF22 - Modificar ítem (si es propietario).
- RF23 - Eliminar ítem (si es propietario).
- RF26 - Sistema de seguidores.
- RF27 - Ranking de usuarios.
- RF28 - Autenticación con Google.
- RF29 - Login biométrico.
- RF30 - Editar perfil de usuario.

---

## 6. Arquitectura

- **Categorías:**
  - id
  - nombre
  - imagen
- **Ítems:**
  - id
  - nombre
  - descripción
  - fecha\_creación
  - likes
  - dislikes
  - id\_usuario
  - id\_categoria
  - latitud
  - longitud
  - like\_por
  - dislike\_por
- **Comentarios:**
  - id
  - texto
  - likes
  - dislikes
  - like\_por
  - dislike\_por
  - id\_usuario
  - id\_item
- **Usuarios:**
  - email
  - nombre\_usuario
  - nombre
  - apellido
  - codigo\_amigo
  - imagen
  - lenguaje
  - ultimo\_login
  - rol
- **Seguimiento:**
  - id
  - seguido
  - seguidor
- **Contacto:**
  - nombre
  - correo\_de
  - correo\_para
  - asunto
  - descripción
  - imagen
  - leído
  - id\_documento
  - fecha

---

## 7. Código

- Repositorio: [GitLab - PokeMap](https://gitlab.com/abp-3/pokemap.git)

---