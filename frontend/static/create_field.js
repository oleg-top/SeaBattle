
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
            console.log(imageUrl)
            button.style.backgroundImage = 'url(' + imageUrl + ')';
            button.style.backgroundSize = 'cover';
        })
        .catch(function(error) {
            console.error('There has been a problem with your fetch operation:', error);
        });

}

async function Delete(ship_id) {
    const formData = new FormData();
    formData.append('id', ship_id);




    try {
        const response = await fetch('http://localhost:8080/ship/delete', {
            method: 'POST',
            body: formData
        });
        const data = await response.json();
        console.log('Got data from Ship Create ', data.id);
        return data.id;
    } catch (error) {
        console.error('Произошла ошибка:', error);
        return null;
    }
}


async function ShipCreate(name, description, file) {
    const formData = new FormData();
    formData.append('name', name);
    formData.append('description', description);
    formData.append('file', file);

    console.log('Ship create data', name, description, file);


    try {
        const response = await fetch('http://localhost:8080/ship/create', {
            method: 'POST',
            body: formData
        });
        const data = await response.json();
        console.log('Got data from Ship Create ', data.id);
        return data.id;
    } catch (error) {
        console.error('Произошла ошибка:', error);
        return null;
    }
}


async function AssignShip(field_id, ship_id, x, y) {
    const data = {
        shipId: ship_id,
        fieldId: parseInt(field_id),
        x: x,
        y: y
    };
    console.log(ship_id, field_id, x, y);
    const dataStringifyed = JSON.stringify(data);
    const url = 'http://localhost:8081/change_field/' + field_id;


    try {
        const response= await fetch(url, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: dataStringifyed
        });
    } catch (error) {
        console.error('Произошла ошибка:', error);
        return null;
    }
}



async function ButtonClick(buttonId) {
    var button = document.getElementById(buttonId);

    IsEmpty = document.getElementById("empty").checked;
    if (IsEmpty){
        //Get information about prize
        result = await Delete(parseInt(button.getAttribute('shipID')));
        var PrizeName        = 'No';
        button.style.backgroundImage = 'url("../../static/src/wave.jpg")';
        button.style.backgroundSize = 'cover';
        button.setAttribute('prize', PrizeName)
    }
    else {
        var PrizeName = document.getElementById('prize_name').value;
        var PrizeDescription = document.getElementById('prize_description').value;
        var PrizeFile = document.getElementById('file').files[0];


        var id = await ShipCreate(PrizeName, PrizeDescription, PrizeFile);
        await console.log('one')
        button.setAttribute('shipID', id)
        var result = await AssignShip(field_id, id, button.getAttribute('x'), button.getAttribute('y'));
        console.log('two')
        button.setAttribute('prize', PrizeName)
        await GetImg(id, buttonId)
    }

    const instance = button._tippy;
    instance.hide();
    instance.setContent('(x: ' + button.getAttribute('x') + ', y: ' + button.getAttribute('y') +') prize: ' + button.getAttribute('prize'));
    instance.show();


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
        console.log(ships_nums.includes(parseInt(button.id)), ships_nums, parseInt(button.id))
        if ( ships_nums.includes(parseInt(button.id)) ) {
            button.setAttribute('prize', ship_information[ships_nums.indexOf(parseInt(button.id))]['name']);
            button.setAttribute('x', x);
            button.setAttribute('y', y);
        }else{
            button.setAttribute('prize', 'No');
            button.setAttribute('x', x);
            button.setAttribute('y', y);
        }


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
        instance.setContent('(x: ' + x + ', y: ' + y +') prize: ' + button.getAttribute('prize'));

        button.addEventListener('click', function() {
            ButtonClick(this.id); // Передаем id в ф-ию обработки клика
        });

        rowDiv.appendChild(button); //Добавляем кнопку к строке
    }
    buttonContainer.appendChild(rowDiv); //Добавляем строку к контейнеру
}
