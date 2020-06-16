// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function randomFact() {
    const facts = ['I am a black belt', 'My favorite color is red',
      'I am from Delaware'];

    const fact = facts[Math.floor(Math.random() * facts.length)];

    const factContainer = document.getElementById('fact-container');
    factContainer.innerText = fact;
}

function fetchJson() {
    fetch('/login').then(loginResponse => loginResponse.json()).then((bool) => {
        const commentContainer = document.getElementById('comment-container');
        const logInContainer = document.getElementById('log-in-container');

        if (!bool) {
            commentContainer.style.display = "none";
            logInContainer.appendChild(createLogInElement());
        } else {
            logInContainer.appendChild(createLogOutElement());
            fetch('/data').then(response => response.json()).then((comments) => {
                comments.forEach((comment) => {
                    console.log(comment);
                    commentContainer.appendChild(createCommentElement(comment));
                });
            });
        }
    }); 
}

function createLogInElement() {
    const logInButton = document.createElement('button');
    logInButton.innerText = 'Log In';
    logInButton.addEventListener('click', () => {
        // redirect to log in page
        window.location.href = "/login-page";
    });

    return logInButton;
}

function createLogOutElement() {
    const logOutButton = document.createElement('button');
    logOutButton.innerText = 'Log Out';
    logOutButton.addEventListener('click', () => {
        // redirect to log out page
        window.location.href = "/login-page";
    });

    return logOutButton;
}

function createCommentElement(comment) {
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';

    const titleElement = document.createElement('span');
    titleElement.innerText = comment.comment;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment);
        commentElement.remove();
    });

    commentElement.appendChild(titleElement);
    commentElement.appendChild(deleteButtonElement);
    return commentElement;
}

function deleteComment(comment) {
    const params = new URLSearchParams();
    params.append('id', comment.id);
    fetch('/delete-task', {method: 'POST', body: params});
}