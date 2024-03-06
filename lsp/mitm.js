/**
 * Man-in-the-middle script that forwards stdio to/from a child command and logs the data.
 * Can be used to wrap cds-lsp (CDS LSP server).
 */

const { spawn } = require('child_process');
const fs = require('fs');
const { Transform, Writable } = require('stream');

const logged = {};

function handle(chunk, prefix) {
    const now = new Date().toISOString();
    let key = `${prefix} ${now}`;
    if (key in logged) {
        key = key.replace(/(?=Z$)/, process.hrtime.bigint().toString().slice(-6));
    }
    let content = chunk.toString().trim();
    if (content.startsWith('{') && content.endsWith('}')) {
        try {
            content = JSON.parse(chunk.toString());
        } catch (error) {
            // maybe incomplete JSON
        }
    }
    logged[key] = content;
}

function writeFile() {
    fs.writeFileSync(logFile, JSON.stringify(logged, null, 2));
}

function createLogPrefixStream(prefix){
    return new Writable({
        write(chunk, encoding, callback) {
            const lines = chunk.toString().trim()
                .split('\n')
                .map(line => line.trim())
                .filter(line => line.length > 0);
            for (const line of lines) {
                handle(line, prefix);
            }
            writeFile();
            callback();
        }
    });
}


if (process.argv.length < 4) {
    console.error('Usage: node mitm.js LOGFILE COMMAND…');
    process.exit(1);
}

const logFile = process.argv[2];
const command = process.argv[3];
const args = process.argv.slice(4);

const child = spawn(command, args);

writeFile();

// Redirect stdin

//   client → MITM → child
process.stdin.pipe(child.stdin);
//   client → MITM → log
process.stdin.pipe(createLogPrefixStream('>'));

// Redirect stdout

//   child → MITM → client
child.stdout.pipe(process.stdout);
//   child → MITM → log
child.stdout.pipe(createLogPrefixStream('<'));

// Redirect stderr

//   child → MITM → client
child.stderr.pipe(process.stderr);
//   child → MITM → log
child.stderr.pipe(createLogPrefixStream('<[E]'));

// Log process events

[process, child].forEach(proc =>
    ['stdin', 'stdout', 'stderr'].forEach(stream => proc[stream].on('close', () => {
        handle(`${proc === child ? 'child' : 'mitm'} ${stream} closed`, '[I]');
    }))
);
child.on('close', (code) => {
    handle(`child process exited with code ${code}`, '[I]');
});
