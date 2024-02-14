import jwt
import requests
from flask import Flask, render_template, request, redirect, make_response, url_for, jsonify
import json

app = Flask(__name__)
API_URL = 'http://localhost:8080'
app.config['SECRET_KEY'] = 'asdfjp982j3r908ajwef908j2faskdfh23iuh23fi2f23fnzxcvh9zxvc'


def get_data(token, mode):
    req = requests.get(f'{API_URL}/data/get_current_user_data', headers={'Authorization': f'Bearer {token}'})
    res = req.json()
    if req.status_code != 200:
        return {'ship': 'error'}
    return res[mode]


@app.route('/')
def landing():
    return make_response(redirect(url_for('login')))


@app.route('/login', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        body = {
            'username': request.form.get('username'),
            'password': request.form.get('password'),
        }
        req = requests.post(f'{API_URL}/authorization', json=body)
        res = req.json()

        if req.status_code != 200:
            # print('something went wrong...', res)
            if 'message' in res.keys() and res['message'] == 'Неправильный логин или пароль':
                error_message = res['message']
                return render_template('authorization/login.html', msg=error_message)
            return render_template('error.html', error=res['message'] if 'message' in res.keys() else req.status_code)

        if 'token' in res.keys():
            # print(res['token'])
            resp = make_response(redirect(url_for('user_profile')))
            resp.set_cookie('token', res['token'], max_age=60 * 60 * 24 * 7 * 2)
            return resp

    token = request.cookies.get('token', None)
    if token is not None:
        return make_response(redirect(url_for('user_profile')))
    return render_template('authorization/login.html')


@app.route('/signup', methods=['POST', 'GET'])
def signup():
    if request.method == 'POST':
        body = {
            'username': request.form.get('username'),
            'password': request.form.get('password'),
            'isAdmin': True if request.form.get('isAdmin') == 'on' else False
        }
        req = requests.post(f'{API_URL}/registration', json=body)
        res = req.json()

        if req.status_code != 200:
            # print('something went wrong...', res)
            if 'message' in res.keys():
                error_message = res['message']
                return render_template('authorization/signup.html', msg=error_message)
            return render_template('error.html', error=res['message'] if 'message' in res.keys() else req.status_code)

        if 'token' in res.keys():
            # print(res['token'])
            resp = make_response(redirect(url_for('user_profile')))
            resp.set_cookie('token', res['token'], max_age=60 * 60 * 24 * 7 * 2)
            return resp

        else:
            return render_template('error.html', error=res['message'])

    token = request.cookies.get('token', None)
    if token is not None:
        return make_response(redirect(url_for('user_profile')))
    return render_template('authorization/signup.html')


@app.route('/logout')
def logout():
    resp = make_response(redirect(url_for('login')))
    resp.delete_cookie('token')
    return resp


@app.route('/user_profile', methods=['POST', 'GET'])
def user_profile():
    token = request.cookies.get('token', None)
    if token is None:
        return redirect(url_for('login'))
    else:
        try:
            decoded_token = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
        except jwt.InvalidSignatureError:
            decoded_token = jwt.decode(token, options={"verify_signature": False}, algorithms=['HS256'])

        prizes = [prz['ship'] for prz in get_data(token, 'prizes')]  # список призов
        fields = [fld['field'] for fld in get_data(token, 'shots')]  # список полей
        if len(prizes) == 0:
            prizes = [
                {
                    'name': 'призов пока нет'
                }
            ]

        kwargs = {
            'username': decoded_token['sub'],
            'id': decoded_token['id'],
            'prizes': prizes[:3],
            'fields': fields[:3]
        }
        role = decoded_token['role']

        if role == 'ADMIN':
            return render_template('profiles/admin_profile.html', **kwargs)
        return render_template('profiles/user_profile.html', **kwargs)


@app.route('/prizes')
def prizes_page():
    token = request.cookies.get('token', None)
    if token is None:
        return redirect(url_for('login'))
    else:
        try:
            decoded_token = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
        except jwt.InvalidSignatureError:
            decoded_token = jwt.decode(token, options={"verify_signature": False}, algorithms=['HS256'])
        prizes = [prz['ship'] for prz in get_data(token, 'prizes')]  # список призов
        kwargs = {
            'prizes': prizes,

        }
        role = decoded_token['role']

        if role == 'ADMIN':
            return redirect(url_for('user_profile'))
        return render_template('profiles/prizes.html', **kwargs)


@app.route('/fields')
def fields_page():
    token = request.cookies.get('token', None)
    if token is None:
        return redirect(url_for('login'))
    else:
        try:
            decoded_token = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
        except jwt.InvalidSignatureError:
            decoded_token = jwt.decode(token, options={"verify_signature": False}, algorithms=['HS256'])
        fields = [fld['field'] for fld in get_data(token, 'shots')]  # список полей
        kwargs = {
            'fields': fields,
        }
        print(kwargs)
        role = decoded_token['role']

        if role == 'ADMIN':
            return redirect(url_for('user_profile'))
        return render_template('profiles/fields.html', **kwargs)




@app.route('/create_field', methods=['GET', 'POST'])
def create_field():
    if request.method == 'POST':
        print(request)
        name = request.form.get('field_name')
        description = request.form.get('field_description')
        size = request.form.get('field_size')
        body = {'name': name, 'description': description, 'size': size}
        print(body)
        req = requests.post(f'http://localhost:8080/field/create', json=body)
        field_id = req.json()['id']
        print(field_id)
        return redirect(url_for('change_field', field_id=field_id))
    print('GO')
    return render_template('create_field.html')


@app.route('/invitation', methods=['GET', 'POST'])
def invite():
    if request.method == 'POST':
        print(request)
        userId = request.form.get('userId')
        fieldId = request.form.get('fieldId')
        amount = request.form.get('amount')
        body = {'userId': userId, 'fieldId': fieldId, 'amount': int(amount)}
        print(body)
        req = requests.post(f'http://localhost:8080/field/invite_user', json=body)
        field_id = req.json()['id']
        print(field_id)
    print('GO')
    return render_template('invitation.html')


@app.route('/change_field/<field_id>', methods=['GET', 'POST'])
def change_field(field_id):
    if request.method == 'POST':
        data = request.data.decode('utf-8')
        json_data = json.loads(data)
        print(json_data)
        req = requests.post(f'http://localhost:8080/field/assign_ship', json=json_data)
        print(req.json())
        # print(json_data)
        # print(json_data['shipId'], json_data['fieldId'], json_data['x'] ,json_data['y'])
    req = requests.request("GET", f'http://localhost:8080/data/get_field_data_by_id/{field_id}')
    req = req.json()
    size = req['field']['size']
    name = req['field']['name']
    description = req['field']['description']
    ships = req['ships']
    ships_nums = []
    ship_information = []
    for ship in ships:
        ships_nums.append(int(ship['x']) + int(ship['y']) * size)
        ship_information.append({'id': ship['id'], 'image': ship['image'], 'description': ship['description'], 'name': ship['name'], 'active': ship['active']})

    print(ships_nums)
    return render_template('change_field.html', field_id=field_id, size=size, name=name, description=description, ships_nums=ships_nums, ship_information=ship_information)


@app.route('/play_field/<field_id>', methods=['GET', 'POST'])
def play_field(field_id):
    if request.method == 'POST':
        data = request.data.decode('utf-8')
        json_data = json.loads(data)
        print(json_data)
        if json_data['type'] == 'shot':
            data = json.dumps({"fieldId": json_data["fieldID"], "x": json_data["x"], "y": json_data["y"]})
            req = requests.post(f'http://localhost:8080/game/take_a_shot', data=data,  headers={'Authorization': f'Bearer {json_data["token"]}', 'Content-Type': 'application/json'})
            print(req.json())
            print(json_data)
            return jsonify(req.json()), 200
        else:
            req = requests.post(f'http://localhost:8080/field/assign_ship', json=json_data)
            print(req.json())
            print(json_data)
            print(json_data['shipId'], json_data['fieldId'], json_data['x'], json_data['y'])
    req = requests.request("GET", f'http://localhost:8080/data/get_field_data_by_id/{field_id}')
    req = req.json()
    print(req)
    size = req['field']['size']
    name = req['field']['name']
    description = req['field']['description']
    ships = req['ships']
    ships_nums = []
    ship_information = []
    for ship in ships:
        ships_nums.append(int(ship['x']) + int(ship['y']) * size)
        ship_information.append({'id': ship['id'], 'image': ship['image'], 'description': ship['description'], 'name': ship['name'], 'active': ship['active']})
    print(ships_nums)
    return render_template('play_field.html', field_id=field_id, size=size, name=name, description=description, ships_nums=ships_nums, ship_information=ship_information)


if __name__ == '__main__':
    app.run(port=8081, host='127.0.0.1', debug=True)
