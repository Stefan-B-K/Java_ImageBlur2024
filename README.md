# ImageBlur2024 (Project 1/3 of Methodia Workshop / Self-training 2024)

## Задача 1

Четене на локално изображение и прилагане на замъгляващ ефект.   
Програмата ви трябва да чете файл от файловата система и да приложи замъгляващ ефект.
Алгоритъмът за замъгляване може да е по ваш избор или да приложите следния алгоритъм:
https://docs.gimp.org/2.10/en/gimp-filter-median-blur.html

````diff
+ ============================ SOLUTION =========================== 
+ Swing App using the solution types ImgReader, ImgFilter and ImgWriter
````
![](https://github.com/Stefan-B-K/Java_ImageBlur2024/blob/main/src/main/resources/images/Screenshot1.png)

## Задача 2
Добавяне на нов тип филтър, който да замъглява по средната стойност на яркостта на всеки пиксел в определен радиус.

````diff
+ ============================ SOLUTION =========================== 
+ Extracting the image pixels parsing and kernel values generation in an 
+ abstract class FilterSquareKernel and subclassing with the new FilterMeanAlpha:
````
![](https://github.com/Stefan-B-K/Java_ImageBlur2024/blob/main/src/main/resources/images/Screenshot2.png)

## Задача 3
Развийте програмата, така че да могат лесно да се добавят нови имплементации на филтри. Всеки филтър да може да бъде рефериран чрез име и параметри.

## Задача 4
Развийте конзолния интерфейс, така че да може да се изреждат филтрите и техните параметри като аргументи на програмата.
Примерни аргументи:
imagepath averagebrightnessblur 3 colorfilter red crop 10 15 100 110
Което може да означава - приложи замъгляване чрез осредняване на яркостта в радиус 3, приложи червен филтър, тоест само R компонентата да остане, изрежи изображението, формирайки правоъгълник 100x110 от позиция x:10 y:15

````diff
+ ========================= SOLUTION ======================== 
+ Drop-down list (for filter constructors) and parameters pane 
+ generated with Reflection API:
````
![](https://github.com/Stefan-B-K/Java_ImageBlur2024/blob/main/src/main/resources/images/Screenshot3.png)

## Задача 5
Възможност пътят до изображението да е зададен чрез HTTP URL.

````diff
+ =================== SOLUTION ===================
+ Final version, working both as Swing and CLI app. 
+ Besides choosing from local file and URL, the user
+ can Google search for an image using 'serpapi.com'. 
````
![](https://github.com/Stefan-B-K/Java_ImageBlur2024/blob/main/src/main/resources/images/Screenshot4.png)
