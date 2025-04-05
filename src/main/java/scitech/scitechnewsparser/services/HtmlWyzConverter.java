package scitech.scitechnewsparser.services;

import org.springframework.stereotype.Component;

import java.util.regex.*;
@Component
public class HtmlWyzConverter {

    public static String convertImgWyzTags(String htmlText) {
        if (htmlText == null || htmlText.isEmpty()) {
            return htmlText;
        }

        Pattern pattern = Pattern.compile("<img-wyz\\s+([^>]+?)\\s*(?:/>|></img-wyz>)");
        Matcher matcher = pattern.matcher(htmlText);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String attributes = matcher.group(1);
            String standardImgTag = convertAttributesToStandardImg(attributes);
            matcher.appendReplacement(result, standardImgTag);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String convertAttributesToStandardImg(String wyzAttributes) {
        // Извлекаем отдельные атрибуты
        String src = extractAttribute(wyzAttributes, "src");
        String align = extractAttribute(wyzAttributes, "align");
        String width = extractAttribute(wyzAttributes, ":width");
        String height = extractAttribute(wyzAttributes, ":height");
        String description = extractAttribute(wyzAttributes, "description");
        String author = extractAttribute(wyzAttributes, "author");

        // Строим стандартный тег img
        StringBuilder imgTag = new StringBuilder("<img");

        // Обязательный атрибут src
        if (src != null) {
            imgTag.append(" src=\"").append(src).append("\"");
        }

        // Атрибут align преобразуем в style float
        if (align != null) {
            imgTag.append(" style=\"float:").append(align).append(";");

            // Добавляем width и height в style, если они есть
            if (width != null && height != null) {
                imgTag.append("width:").append(width).append("px;height:").append(height).append("px;");
            }
            imgTag.append("\"");
        } else if (width != null && height != null) {
            // Если нет align, но есть width/height
            imgTag.append(" style=\"width:").append(width).append("px;height:").append(height).append("px;\"");
        }

        // Атрибут alt (используем description или author)
        String altText = description != null ? description : author != null ? author : "";
        if (!altText.isEmpty()) {
            imgTag.append(" alt=\"").append(escapeHtml(altText)).append("\"");
        }

        imgTag.append(">");

        return imgTag.toString();
    }

    private static String extractAttribute(String attributes, String attrName) {
        // Ищем атрибут в кавычках (двойных или одинарных)
        Pattern pattern = Pattern.compile(attrName + "=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(attributes);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}