const fs = require('fs');
const express = require('express');
const parser = require('body-parser');

const PORT = 42802;

function uuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const app = express();

app.use(parser.raw({ inflate: true, limit: '100mb', type: '*/*' }));

app.post('/create', (req, res) => {
    let name = req.get('Name');
    let extension = req.get('Extension');
    if (!name) name = uuid();
    if (!extension) {
        res.end('no-extension');
    } else {
        let fileName = `${name}.${extension}`;
        let filePath = `${__dirname}/images/${fileName}`;
        fs.exists(filePath, (exists) => {
            if (exists) {
                res.end('exists');
            } else {
                fs.writeFile(filePath, req.body, () => {
                    res.end(`success,${fileName}`);
                });
            }
        });
    }
});

app.get('*', (req, res) => {
    const filePath = `${__dirname}/images${req.originalUrl}`;
    fs.exists(filePath, (exists) => {
        if (exists) {
            const stats = fs.statSync(filePath);
            if (stats.isFile()) {
                res.sendFile(filePath);
                return;
            }
        }
        res.sendFile(`${__dirname}/not-found.html`);
    });
});

app.listen(PORT, () => {
    console.log(`Server now listening on port ${PORT}`);
});