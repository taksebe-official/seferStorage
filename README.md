## Sefer

Простой модуль, используемый через API внешними приложениями для централизованного хранения файлов в заданной директории

Sefer - "книга" на древнееврейском

## Особенности:

-   файлы некоторых форматов, указанных в настройках, архивируются (.zip), остальных форматов - нет
-   файлы хранятся без расширения и оригинального имени. Эти параметры хранит внешнее приложение
-   при сохранении файлу в качестве имени присваивается UUID, служащий идентификатором файла для внешних приложений

## API

### Загрузить файл:

<ins>HTTP Method</ins>: POST

<ins>URL</ins>: `.../sefer/api/files/upload`

<ins>Request:</ins> MultipartFile, не забывая записать в поле name объекта MultipartFile полное название файла, включающее его расширение (например, `"Резюме Торвальдс.pdf"`)

<ins>Response</ins>:

```json
{
"response": {
    "FileInfoDto": {
        fileName: <UUID>
        }
     }
}
```

### Получить файл:

<ins>HTTP Method</ins>: GET

<ins>URL</ins>: `.../api/files/download/{uuid}`

<ins>Request:</ins> UUID

<ins>Response</ins>: application/octet-stream

### Удалить файл:

<ins>HTTP Method</ins>: DELETE

<ins>URL</ins>: `.../api/files/{uuid}`

<ins>Request:</ins> UUID

<ins>Response</ins>: void

## Пример использования

```java
    public UUID upload(MultipartFile file) {
        String serverUrl = URL + "/upload";

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
        parameters.set("Content-Type","multipart/form-data");
        parameters.add("file", file.getResource());

        final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(
                parameters, requestHeaders);

        ResponseEntity<FileInfoDto> response = restTemplate.exchange(serverUrl, HttpMethod.POST, httpEntity, FileInfoDto.class);

        if (response.getBody() == null) {
            return null;
        }
        return response.getBody().getFileName();
    }

    public void download(UUID seferFileName, OutputStream stream) {
        String serverUrl = URL + "/download/" + seferFileName;

        restTemplate.execute(serverUrl, HttpMethod.GET, null, clientHttpResponse -> {
            StreamUtils.copy(clientHttpResponse.getBody(), stream);
            return stream;
        });
    }

    public void delete(UUID seferFileName) {
        String serverUrl = URL + "/" + seferFileName;
        restTemplate.delete(serverUrl);
    }
```

## Лицензия

Этот проект лицензируется в соответствии с лицензией Apache 2.0

Подробности в файле LICENSE

## Автор

Сергей Козырев

## Контакты для связи

Telegram [@taksebe](https://t.me/taksebe)

## Создано с помощью

Java™ SE Development Kit 11.0.5

Git - управление версиями

GitHub - репозиторий

[Apache Maven](https://maven.apache.org/) - сборка, управление зависимостями

[JUnit 5](https://junit.org/junit5/) - тестирование

Полный список зависимостей и используемые версии компонентов можно найти в pom.xml

## Сборка и запуск

```
git clone https://github.com/taksebe-official/seferStorage
mvn clean install
java -jar target/sefer.jar
```

## Отдельное спасибо

[Владу](https://github.com/itotx), который продолжает возиться со мной, неразумным
