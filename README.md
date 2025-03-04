# PokeMap

## Índice
1. Introducción
2. Descripción del Proyecto
3. Objetivos de la App
4. Público Objetivo
5. Funcionalidades
6. Pantalla de Login / Registro
7. Lista de Categorías
8. Detalle de Categoría
9. Mapa de Ítems
10. Requisitos Extra
11. Documentación Técnica
12. Requisitos Funcionales por Rol
    - Administrador
    - Usuario
13. Diseño de Base de Datos NoSQL
14. Programación
15. Control de Versiones
16. Demo Video
17. Portfolio
18. Informe del Proyecto
19. FOL
20. ODS (Objetivos de Desarrollo Sostenible)
21. Mapa de Empatía
22. Análisis DAFO
23. Líneas Futuras

---

## 1. Introducción
### Descripción del Proyecto
**PokeMap** es una aplicación diseñada como un foro geolocalizado para jugadores de **Pokémon GO**. Los usuarios podrán compartir ubicaciones clave como nidos, gimnasios, spawn de legendarios y más. Incluye un sistema de seguidores, un buscador avanzado y filtros personalizados.

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
- Registro con datos completos (usuario, contraseña, equipo, código amigo).
- Recuperar contraseña.
- Login con Google.
- Login biométrico.

### Lista de Categorías
- Listado completo de categorías (nidos, gimnasios, spawn legendarios, etc.).
- Filtros por nombre.
- Acceso al detalle de cada categoría.
- Creación, modificación y eliminación de categorías (solo admin).

### Detalle de Categoría
- Vista completa de una categoría.
- Listado de ítems asociados.
- Filtros avanzados de ítems (nombre, distancia, valoración, etc.).
- Ordenar ítems por distancia, valoración o fecha.

### Mapa de Ítems
- Mapa interactivo con la ubicación de los ítems.
- Pinchando en un ítem muestra su información ampliada.
- Permite crear un nuevo ítem desde la ubicación actual.

---

## 5. Requisitos Extra
- Integración con Google Authentication.
- Sistema de seguidores (seguir a otros jugadores y ver solo sus publicaciones).
- Ranking diario de usuarios.

---

## 6. Documentación Técnica
### Requisitos Funcionales por Rol

#### Administrador
- RF01 - Login y Logout.
- RF02 - Recuperar contraseña.
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
- RF24 - Eliminar valoración.
- RF25 - Censurar comentarios.
- RF26 - Sistema de seguidores.
- RF27 - Ranking diario de usuarios.
- RF28 - Autenticación con Google.
- RF29 - Login biométrico.

#### Usuario
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
- RF27 - Ranking diario de usuarios.
- RF28 - Autenticación con Google.
- RF29 - Login biométrico.

---

## 7. Diseño de Base de Datos NoSQL
- Colección: Categorías
    - id
    - nombre
    - descripción
    - imagen
- Colección: Ítems
    - id
    - titulo
    - descripción
    - categoría_id
    - autor
    - fecha_creación
    - posición_gps
    - imagen
    - likes
    - dislikes
- Colección: Usuarios
    - id
    - nombre
    - usuario
    - email
    - código_amigo
    - equipo
    - seguidores (array)
- Colección: Valoraciones
    - id
    - ítem_id
    - usuario_id
    - fecha
    - comentario
    - valoración (like/dislike)

---

## 8. Programación
- Repositorio: [GitLab - PokeMap](https://gitlab.com/MarcCristobal/museumizeme.git)

---

## 9. Control de Versiones
- Git con flujo GitFlow.
- Versiones documentadas en el repositorio.

---

## 10. Demo Video
- [Ver demo](https://drive.google.com/file/d/148zpKYE2kepvQhclXB21EGVkBQbuLB_H/view?usp=sharing)

---

## 11. Portfolio
- [Ver portfolio](https://github.com/MarcCristobal)

---

## 12. Informe del Proyecto
- [Ver informe](https://drive.google.com/file/d/1Qscoz9I8gjjt4vVKhfPxFvuruXnLPtmK/view?usp=sharing)

---

## 13. FOL
- Documentación de formación y orientación laboral vinculada al proyecto.

---

## 14. ODS (Objetivos de Desarrollo Sostenible)
- **4. Educación de calidad:** Promover el aprendizaje colaborativo sobre el entorno del juego.
- **10. Reducción de desigualdades:** Democratizar el acceso a la información relevante sobre el juego.

---

## 15. Mapa de Empatía
### Usuario
- **Nombre:** Entrenador Pokémon GO.
- **Edad:** 13-40 años.
- **Experiencia:** Jugador activo.
- **Necesidad:** Información rápida y actualizada sobre ubicaciones clave.

### Pensamientos y sentimientos
- Curiosidad por encontrar nuevos Pokémon y ubicaciones.
- Frustración por información desactualizada o inexacta.

### Vistas
- Espera una app sencilla y directa.
- Desea mapas claros y filtros efectivos.

### Acciones
- Comparte ubicaciones y experiencias.
- Utiliza la app para planificar rutas de juego.

---

## 16. DAFO
### Fortalezas
- Geolocalización.
- Filtros avanzados.
- Comunidad activa.

### Debilidades
- Dependencia de geolocalización.
- Información dependiente de usuarios.

### Oportunidades
- Expansión internacional.
- Integración con redes sociales.

### Amenazas
- Competencia.
- Cambios en políticas de privacidad.

---

## 17. Líneas Futuras
- Mejoras en la UI/UX.
- Notificaciones personalizadas.
- Función de favoritos.
- Soporte multilingüe.