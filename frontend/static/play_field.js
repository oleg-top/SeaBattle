
async function GetImg(ship_id, buttonId){

    var button = document.getElementById(buttonId);
    var url = "http://localhost:8080/data/images/ships/" + ship_id; // URL для GET-запроса
    console.log(url)
    fetch(url)
        .then(function(response) {
            return response.blob(); // Получаем бинарные данные
        })
        .then(function(blob) {
            var imageUrl = URL.createObjectURL(blob);
            console.log(imageUrl, buttonId, button)
            button.style.backgroundImage = 'url(' + imageUrl + ')';
            button.style.backgroundSize = 'cover';
        })
        .catch(function(error) {
            console.log('There has been a problem with your fetch operation:', error);
        });
    return '1'
}


async function Shot(token, field_id, x, y){

    data_stringifyed = JSON.stringify({type: "shot", fieldID: parseInt(field_id), x: parseInt(x), y: parseInt(y), token: token})
    console.log(token, data_stringifyed)

    const url = 'http://localhost:8081/play_field/' + field_id;


    try {
        const response= await fetch(url, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: data_stringifyed,
        });
        const data = await response.json();
        console.log('Got data from Shot ', data);
        return data;
    } catch (error) {
        console.error('Произошла ошибка:', error);
        return null;
    }
}





async function ButtonClick(buttonId) {
    var button = document.getElementById(buttonId);

    var allCookies = await document.cookie.trim();
    console.log(allCookies)
    var token = await allCookies.slice(6);
    console.log(token)


    var result =  await Shot(token, field_id, button.getAttribute('x'), button.getAttribute('y'));
    status_ship = result.status
    console.log('fff', status_ship)
    console.log(result, status_ship)

        if (result.message == 'The amount of shots is 0'){
            Swal.fire({
                title: 'Выстрелов больше нет!',
                text: 'Возвращайтесь позже ',
                icon: 'warning', // Иконка (success, error, warning, info)
                confirmButtonText: 'Понятно'
            });
        }

        if (status_ship == "HIT") {
            console.log('AAA', result.prize.ship.id, buttonId)
            GetImg(result.prize.ship.id, buttonId)
            const instance = button._tippy;
            button.setAttribute('prize', result.prize.ship.name)
            instance.hide()
            instance.setContent('(x: ' + button.getAttribute('x') + ', y: ' + button.getAttribute('y') + ') prize: ' + result.prize.ship.name);
            instance.show();

            Swal.fire({
                title: 'Похоже вы что-то нашли!',
                text: 'Узнать подробную информацию о вашем призе можно узнать в вашем личном кабинете',
                icon: 'success', // Иконка (success, error, warning, info)
                confirmButtonText: 'Понятно'
            });

        } else if (status_ship == "MISS") {
            button_size = 50 + (26 - size) * 5
            button.style.width = button_size + "px";
            button.style.height = button_size + "px";
            button.style.backgroundImage = 'url("../../static/src/looser.png")';
            button.style.backgroundSize = 'cover';

            Swal.fire({
                title: 'Здесь пусто!',
                text: 'Попытайтесь еще раз',
                icon: 'warning', // Иконка (success, error, warning, info)
                confirmButtonText: 'Понятно'
            });

    }


    console.log(button.id);
    return 'Click';
}



ships = []

console.log(ships_nums)
console.log(ship_information)

var buttonContainer = document.getElementById("field"); //Создаем объект контейнера кнопки
buttonContainer.innerHTML = ""; //Опустошаем контейнер

for (var y = 0; y < size; y++) { //Перебор строк

    var rowDiv = document.createElement("div"); //Создаем строку
    rowDiv.className = "row"; //Присваиваем класс

    for (var x = 0; x < size; x++) { //Перебор столбцов

        var button = document.createElement("button"); //Создаем кнопку
        button.type = "image";
        button.id = parseInt(y * size + x); //Создаю id кнопки
        rowDiv.appendChild(button);
        console.log(ships_nums.includes(parseInt(button.id)), ships_nums, parseInt(button.id))
        if (ships_nums.includes(parseInt(button.id))){
            console.log(ship_information[ships_nums.indexOf(parseInt(button.id))].active)
            if (ship_information[ships_nums.indexOf(parseInt(button.id))].active  == true) {
                id = ship_information[ships_nums.indexOf(parseInt(button.id))].id
                name = ship_information[ships_nums.indexOf(parseInt(button.id))].name
                console.log(id, name)

                button.setAttribute('prize', name);
                button.setAttribute('x', x);
                button.setAttribute('y', y);


                button_size = 50 + (26 - size) * 5
                button.style.width = button_size + "px";
                button.style.height = button_size + "px";
                GetImg(id, button.id)


                const instance = tippy(button)
                instance.setProps({
                    followCursor: true,
                    delay: [1, 1],
                    duration: 10,
                    hideOnClick: false,
                });
                instance.setContent('(x: ' + x + ', y: ' + y + ') prize: ' + button.getAttribute('prize'));
            }

            button.setAttribute('prize', '***');
            button.setAttribute('x', x);
            button.setAttribute('y', y);


            button_size = 50 + (26 - size) * 5
            button.style.width = button_size + "px";
            button.style.height = button_size + "px";
            button.style.backgroundImage = 'url("../../static/src/wave.jpg")';
            button.style.backgroundSize = 'cover';


            const instance = tippy(button)
            instance.setProps({
                followCursor: true,
                delay: [1, 1],
                duration: 10,
                hideOnClick: false,
            });
            instance.setContent('(x: ' + x + ', y: ' + y + ') prize: ' + button.getAttribute('prize'));

        }
        else {
            button.setAttribute('prize', '***');
            button.setAttribute('x', x);
            button.setAttribute('y', y);


            button_size = 50 + (26 - size) * 5
            button.style.width = button_size + "px";
            button.style.height = button_size + "px";
            button.style.backgroundImage = 'url("../../static/src/wave.jpg")';
            button.style.backgroundSize = 'cover';


            // const instance = tippy(button)
            // instance.setProps({
            //     followCursor: true,
            //     delay: [1, 1],
            //     duration: 10,
            //     hideOnClick: false,
            // });
            // instance.setContent('(x: ' + x + ', y: ' + y + ') prize: ' + button.getAttribute('prize'));
        }


        button.addEventListener('click', function() {
            ButtonClick(this.id); // Передаем id в ф-ию обработки клика
        });
        rowDiv.appendChild(button); //Добавляем кнопку к строке

         //Добавляем кнопку к строке
    }
    buttonContainer.appendChild(rowDiv); //Добавляем строку к контейнеру
}



for (var y = 0; y < size; y++) { //Перебор строк

    var rowDiv = document.createElement("div"); //Создаем строку
    rowDiv.className = "row"; //Присваиваем класс

    for (var x = 0; x < size; x++) { //Перебор столбцов

        buttonId = parseInt(y * size + x); //Создаю id кнопки
        button = document.getElementById(buttonId);
        button.type = "image";
        console.log(ships_nums.includes(parseInt(button.id)), ships_nums, parseInt(button.id))
        if (ships_nums.includes(parseInt(button.id))) {
            console.log(ship_information[ships_nums.indexOf(parseInt(button.id))].active)
            if (ship_information[ships_nums.indexOf(parseInt(button.id))].active == false) {
                if (ship_information[ships_nums.indexOf(parseInt(button.id))].description != "Здесь ничего нет"){
                    id = ship_information[ships_nums.indexOf(parseInt(button.id))].id
                    name = ship_information[ships_nums.indexOf(parseInt(button.id))].name
                    console.log(id, name)

                    button.setAttribute('prize', name);
                    button.setAttribute('x', x);
                    button.setAttribute('y', y);


                    button_size = 50 + (26 - size) * 5
                    button.style.width = button_size + "px";
                    button.style.height = button_size + "px";
                    GetImg(id, button.id)


                    const instance = tippy(button)
                    instance.hide();
                    instance.setContent('(x: ' + x + ', y: ' + y + ') prize: ' + button.getAttribute('prize'));
                    instance.show();
                }
                else{
                    id = ship_information[ships_nums.indexOf(parseInt(button.id))].id
                    name = ship_information[ships_nums.indexOf(parseInt(button.id))].name
                    console.log(id, name)

                    button.setAttribute('prize', 'Пусто');
                    button.setAttribute('x', x);
                    button.setAttribute('y', y);


                    button_size = 50 + (26 - size) * 5
                    button.style.width = button_size + "px";
                    button.style.height = button_size + "px";
                    button.style.backgroundImage = 'url("../../static/src/looser.png")';
                    button.style.backgroundSize = 'cover';

                    const instance = tippy(button)
                    instance.hide();
                    instance.setContent('(x: ' + x + ', y: ' + y + ') prize: ' + button.getAttribute('prize'));
                    instance.show();



                }
            }
        }
    }
}