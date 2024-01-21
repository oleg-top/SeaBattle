from flask import Flask, render_template, render_template_string, request, redirect, make_response, url_for
import jwt
import requests

app = Flask(__name__)
API_URL = 'http://localhost:8080'
app.config['SECRET_KEY'] = 'asdfjp982j3r908ajwef908j2faskdfh23iuh23fi2f23fnzxcvh9zxvc'


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


# TODO: отображение призов и полей
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
        kwargs = {
            'username': decoded_token['sub'],
            'id': decoded_token['id'],
        }
        return render_template('profiles/user_profile.html', **kwargs)


def get_prizes(token):
    req = requests.get(f'{API_URL}/data/get_current_user_data', headers={'Authorization': f'Bearer {token}'})
    res = req.json()
    if req.status_code != 200:
        return render_template('error.html', error='Ошибка при загрузке данных')


if __name__ == '__main__':
    app.run(port=1488, host='127.0.0.1', debug=True)
